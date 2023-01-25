package org.powbot.dax.engine.interaction;

import org.powbot.api.Random;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.Loggable;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.shared.helpers.General;
import org.powbot.mobile.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class NPCInteraction implements Loggable {

    public static String[] GENERAL_RESPONSES = {"Sorry, I'm a bit busy.", "OK then.", "Yes.", "Okay..."};

    private static final int
            ITEM_ACTION_INTERFACE_WINDOW = 193,
            NPC_TALKING_INTERFACE_WINDOW = 231,
            PLAYER_TALKING_INTERFACE_WINDOW = 217,
            SELECT_AN_OPTION_INTERFACE_WINDOW = 219,
            SINGLE_OPTION_DIALOGUE_WINDOW = 229;

    private static final int[] ALL_WINDOWS = new int[]{ITEM_ACTION_INTERFACE_WINDOW, NPC_TALKING_INTERFACE_WINDOW, PLAYER_TALKING_INTERFACE_WINDOW, SELECT_AN_OPTION_INTERFACE_WINDOW, SINGLE_OPTION_DIALOGUE_WINDOW};


    private static NPCInteraction instance;

    private NPCInteraction(){

    }

    private static NPCInteraction getInstance(){
        return instance != null ? instance : (instance = new NPCInteraction());
    }

    /**
     *
     * @param rsnpcFilter
     * @param talkOptions
     * @param replyAnswers
     * @return
     */
    public static boolean talkTo(Predicate<Npc> rsnpcFilter, String[] talkOptions, String[] replyAnswers) {
        if (!clickNpcAndWaitChat(rsnpcFilter, talkOptions)){
            return false;
        }
        handleConversation(replyAnswers);
        return true;
    }

    /**
     *
     * @param rsnpcFilter
     * @param options
     * @return
     */
    public static boolean clickNpcAndWaitChat(Predicate<Npc> rsnpcFilter, String... options) {
        return clickNpc(rsnpcFilter, options) && waitForConversationWindow();
    }

    public static boolean clickNpc(Predicate<Npc> rsnpcFilter, String... options) {
        List<Npc> rsnpcs = Npcs.stream().filter(rsnpcFilter).nearest().list();
        if (rsnpcs.size() < 1) {
            getInstance().log("Cannot find NPC.");
            return false;
        }

        Npc npc = rsnpcs.get(0);
        for (String opt : options) {
            if (InteractionHelper.click(npc, opt)) return true;
        }
        return false;
    }

    public static boolean waitForConversationWindow(){
        Player player = Players.local();
        Actor rsCharacter = null;
        if (player != Player.getNil()){
            rsCharacter = player.interacting();
        }
        return WaitFor.condition(rsCharacter != null ? WaitFor.getMovementRandomSleep(rsCharacter) : 10000, () -> {
            if (isConversationWindowUp()) {
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

    public static boolean isConversationWindowUp(){
        return Arrays.stream(ALL_WINDOWS).anyMatch(i -> {
            if(i == SINGLE_OPTION_DIALOGUE_WINDOW){
                Component curr = Widgets.widget(i).getComponents().stream().filter(t -> {
                    String txt = t.text();
                    return txt.length() > 0 && t.valid() && t.visible();
                }).findFirst().orElse(null);
                return curr != null;
            }
            Component comp = Widgets.component(i, 0);
            if(comp.valid() && comp.visible()){
                System.out.println("Widget root: " + i + " is up.");
                return true;
            }
            return false;
        });
    }

    public static void handleConversationRegex(String regex){
        while (true){
            if (WaitFor.condition(Random.nextInt(650, 800), () -> isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                break;
            }

            if (getClickHereToContinue() != null){
                clickHereToContinue();
                continue;
            }

            List<Component> selectableOptions = getAllOptions(regex);
            if (selectableOptions == null || selectableOptions.size() == 0){
                WaitFor.milliseconds(100);
                continue;
            }

            WaitFor.milliseconds(General.randomSD(350, 2250, 775, 350));
            getInstance().log("Replying with option: " + selectableOptions.get(0).text());
//            Keyboard.typeString(selectableOptions.get(0).getIndex() + "");
            selectableOptions.get(0).click();
            waitForNextOption();
        }
    }

    public static void handleConversation(String... options){
        getInstance().log("Handling... " + Arrays.asList(options));
        List<String> blackList = new ArrayList<>();
        int limit = 0;
        while (limit++ < 50){
            if (WaitFor.condition(Random.nextInt(650, 800), () -> isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                getInstance().log("Conversation window not up.");
                break;
            }

            if (getClickHereToContinue() != null){
                clickHereToContinue();
                limit = 0;
                continue;
            }

            List<Component> selectableOptions = getAllOptions(options);
            if (selectableOptions == null || selectableOptions.size() == 0){
                WaitFor.milliseconds(150);
                continue;
            }

            for (Component selected : selectableOptions){
                if(blackList.contains(selected.text())){
                    continue;
                }
                WaitFor.milliseconds(General.randomSD(350, 2250, 775, 350));
                getInstance().log("Replying with option: " + selected.text());
                blackList.add(selected.text());
                Keyboard.INSTANCE.type(selected.index() + "");
                waitForNextOption();
                limit = 0;
                break;
            }
            WaitFor.milliseconds(20,40);
        }
        if(limit > 50){
            getInstance().log("Reached conversation limit.");
        }
    }

    /**
     *
     * @return Click here to continue conversation interface
     */
    private static Component getClickHereToContinue(){
        List<Component> list = getConversationDetails();
        if (list == null){
            return null;
        }
        Optional<Component> optional = list.stream().filter(comp -> comp.text().equals("Tap here to continue")).findAny();
        return optional.orElse(null);
    }

    /**
     * Presses space bar
     */
    private static void clickHereToContinue(){
        getInstance().log("Clicking continue.");
        Chat.clickContinue();
        waitForNextOption();
    }

    /**
     * Waits for chat conversation text change.
     */
    private static void waitForNextOption(){
        List<String> interfaces = getAllInterfaces().stream().map(Component::text).collect(Collectors.toList());
        WaitFor.condition(5000, () -> {
            if (!interfaces.equals(getAllInterfaces().stream().map(Component::text).collect(Collectors.toList()))){
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        });
    }

    /**
     *
     * @return List of all reply-able interfaces that has valid text.
     */
    private static List<Component> getConversationDetails(){
        for (int window : ALL_WINDOWS){
            List<Component> details = Components.stream(window).filter(c -> c.text().length() > 0 && c.valid() && c.visible()).list();
            System.out.println("Grabbing interfaces for window: " + window + ", list size: " + details.size());
            if (details.size() > 0) {
                getInstance().log("Conversation Options: [" + details.stream().map(Component::text).collect(
                        Collectors.joining(", ")) + "]");
                return details;
            }
        }
        return null;
    }

    /**
     *
     * @return List of all Chat interfaces
     */
    private static List<Component> getAllInterfaces(){
        ArrayList<Component> interfaces = new ArrayList<>();
        for (int window : ALL_WINDOWS) {
            interfaces.addAll(Components.stream(window).list());
        }

        return interfaces;
    }

    /**
     *
     * @param regex
     * @return list of conversation clickable options that matches {@code regex}
     */
    private static List<Component> getAllOptions(String regex){
        List<Component> list = getConversationDetails();
        return list != null ? list.stream().filter(c -> c.text().matches(regex)).collect(
                Collectors.toList()) : null;
    }

    /**
     *
     * @param options
     * @return list of conversation clickable options that is contained in options.
     */
    private static List<Component> getAllOptions(String... options){
        final List<String> optionList = Arrays.stream(options).map(String::toLowerCase).collect(Collectors.toList());
        List<Component> list = getConversationDetails();
        return list != null ? list.stream().filter(comp -> optionList.contains(comp.text().trim().toLowerCase())).collect(
                Collectors.toList()) : null;
    }

    @Override
    public String getName() {
        return "NPC Interaction";
    }

}
