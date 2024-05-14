package org.powbot.dax.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.powbot.dax.api.json.Json;
import org.powbot.dax.api.json.JsonValue;
import org.powbot.dax.api.json.ParseException;
import org.powbot.dax.api.models.*;
import org.powbot.dax.engine.Loggable;
import org.powbot.mobile.service.gob.DaxProxyService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.*;

public class WebWalkerServerApi implements Loggable {

    private static WebWalkerServerApi webWalkerServerApi;
    private static Gson gson = new Gson();

    public static WebWalkerServerApi getInstance() {
        return webWalkerServerApi != null ? webWalkerServerApi : (webWalkerServerApi = new WebWalkerServerApi());
    }

    private static final String WALKER_ENDPOINT = "https://walker.dax.cloud", TEST_ENDPOINT = "http://localhost:8080";

    private static final String
            GENERATE_PATH = "/walker/generatePath",
            GENERATE_BANK_PATH = "/walker/generateBankPath";


    private HashMap<String, String> cache;
    private boolean isTestMode;

    private WebWalkerServerApi() {
        cache = new HashMap<>();
    }

    public List<PathResult> getPaths(BulkPathRequest bulkPathRequest) {
        try {
            return parseResults(post(gson.toJson(bulkPathRequest),WALKER_ENDPOINT + "/walker/generatePaths"));
        } catch(IOException|UncheckedIOException e){
            getInstance().log("Is server down? Spam dax.");
            return Collections.singletonList(new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER));
        }
    }

    public List<PathResult> getBankPaths(BulkBankPathRequest bulkBankPathRequest) {
        try {
            return parseResults(post(gson.toJson(bulkBankPathRequest),WALKER_ENDPOINT + "/walker/generateBankPaths"));
        } catch(IOException|UncheckedIOException e){
            getInstance().log("Is server down? Spam dax.");
            return Collections.singletonList(new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER));
        }
    }

    public PathResult getPath(Point3D start, Point3D end, PlayerDetails playerDetails) {
        com.google.gson.JsonObject pathRequest = new com.google.gson.JsonObject();
        pathRequest.add("start", start.toJson());
        pathRequest.add("end", end.toJson());

        if (playerDetails != null) {
            pathRequest.add("player", playerDetails.toJson());
        }

        try {
            return parseResult(post(pathRequest, (isTestMode ? TEST_ENDPOINT : WALKER_ENDPOINT) + GENERATE_PATH));
        } catch (IOException|UncheckedIOException e) {
            getInstance().log("Is server down? Spam dax.");
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER);
        }

    }

    public PathResult getBankPath(Point3D start, RunescapeBank bank, PlayerDetails playerDetails) {
        com.google.gson.JsonObject pathRequest = new com.google.gson.JsonObject();

        pathRequest.add("start", start.toJson());

        if (bank != null) {
            pathRequest.addProperty("bank", bank.toString());
        }

        if (playerDetails != null) {
            pathRequest.add("player", playerDetails.toJson());
        }

        try {
            return parseResult(post(pathRequest, (isTestMode ? TEST_ENDPOINT : WALKER_ENDPOINT) + GENERATE_BANK_PATH));
        } catch (IOException|UncheckedIOException e) {
            getInstance().log("Is server down? Spam dax.");
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER);
        }
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    private List<PathResult> parseResults(ServerResponse serverResponse){
        if (!serverResponse.isSuccess()) {
            JsonValue jsonValue  = null;
            try{
                jsonValue = Json.parse(serverResponse.getContents());
            } catch(Exception | Error e){
                jsonValue = Json.NULL;
            }
            if (!jsonValue.isNull()) {
                getInstance().log("[Error] " + jsonValue.asObject().getString(
                        "message",
                        "Could not generate path: " + serverResponse.getContents()
                                                                             ));
            }

            switch (serverResponse.getCode()) {
                case 502:
                    log("Error: HTTP 502 from cloudflare.  This is an issue with the powbot proxy service.");
                case 429:
                    return Collections.singletonList(new PathResult(PathStatus.RATE_LIMIT_EXCEEDED));
                case 400:
                case 401:
                case 404:
                    return Collections.singletonList(new PathResult(PathStatus.INVALID_CREDENTIALS));
            }
        }

        try {
            return gson.fromJson(serverResponse.getContents(), new TypeToken<List<PathResult>>() {}.getType());
        } catch (ParseException| JsonSyntaxException e) {
            PathResult pathResult = new PathResult(PathStatus.UNKNOWN);
            log("Error: " + pathResult.getPathStatus() + ", " + serverResponse.getContents());
            return Collections.singletonList(pathResult);
        }
    }

    private PathResult parseResult(ServerResponse serverResponse) {
        if (!serverResponse.isSuccess()) {
            JsonValue jsonValue  = null;
            try{
                jsonValue = Json.parse(serverResponse.getContents());
            } catch(Exception | Error e){
                jsonValue = Json.NULL;
            }
            if (!jsonValue.isNull()) {
                getInstance().log("[Error] " + jsonValue.asObject().getString(
                        "message",
                        "Could not generate path: " + serverResponse.getContents()
                ));
            }

            switch (serverResponse.getCode()) {
                case 429:
                    return new PathResult(PathStatus.RATE_LIMIT_EXCEEDED);
                case 400:
                case 401:
                case 404:
                    return new PathResult(PathStatus.INVALID_CREDENTIALS);
            }
        }

        PathResult pathResult;
        JsonElement jsonObject;
        try {
            jsonObject = new JsonParser().parse(serverResponse.getContents());
        } catch (ParseException e) {
            pathResult = new PathResult(PathStatus.UNKNOWN);
            log("Error: " + pathResult.getPathStatus());
            return pathResult;
        }

        pathResult = PathResult.fromJson(jsonObject);
        log("Response: " + pathResult.getPathStatus() + " Cost: " + pathResult.getCost());
        return pathResult;
    }

    private ServerResponse post(com.google.gson.JsonObject jsonObject, String endpoint) throws IOException {
        return post(gson.toJson(jsonObject),endpoint);
    }

    private ServerResponse post(String json, String endpoint) throws IOException {
        getInstance().log("Generating path: " + json);
        if (cache.containsKey(json)) {
            return new ServerResponse(true, HttpURLConnection.HTTP_OK, cache.get(json.toString()));
        }

        String resp = DaxProxyService.INSTANCE.executePostRequest(endpoint, json);

        if (resp == null) {
            return new ServerResponse(false, -1, null);
        }
        try {

            Map<String, String>[] responseJson = new Gson().fromJson(resp, new TypeToken<Map<String, Object>[]>() {}.getType());

            String statusCode = responseJson[0].get("status");
            if(!Objects.equals(statusCode, "429")){
                cache.put(json, resp);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return new ServerResponse(true, HttpURLConnection.HTTP_OK, resp);
    }


    @Override
    public String getName() {
        return "DaxWalker";
    }


}
