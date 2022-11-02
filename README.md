# walking-api

Many teleports are blacklisted by default to aid in reducing computation time of each call.  With those teleports blacklisted, one can expect a call to take roughly .7-1 sec to generate player details, send the request, and receive a response.  I took care to only blacklist teleports that are used in specific scenarios, and leave the generally accessible ones available.  
<details>
<summary>Here are the blacklisted teleports</summary>

```
Teleport.NARDAH_TELEPORT
Teleport.LUNAR_ISLE_TELEPORT
Teleport.ELF_CAMP_TELEPORT
Teleport.KEY_MASTER_TELEPORT
Teleport.BURNING_AMULET_BANDIT_CAMP
Teleport.ECTOPHIAL
Teleport.WEST_ARDOUGNE_TELEPORT_TAB
Teleport.CRAFTING_CAPE_TELEPORT
Teleport.RIMMINGTON_TELEPORT_TAB
Teleport.POLLNIVNEACH_TELEPORT_TAB
Teleport.CONSTRUCTION_CAPE_BRIMHAVEN
Teleport.CONSTRUCTION_CAPE_TAVERLEY
Teleport.SLAYER_RING_MORYTANIA
Teleport.SALVE_GRAVEYARD_TAB
Teleport.BATTLEFRONT_TAB
Teleport.FARMING_CAPE_TELEPORT
Teleport.OURANIA_TELEPORT_TAB
Teleport.KHAZARD_TELEPORT_TAB
Teleport.DIGSITE_TELEPORT
Teleport.MORTTON_TELEPORT
Teleport.MOS_LE_HARMLESS_TELEPORT
Teleport.REVENANT_CAVES_TELEPORT
Teleport.BURNING_AMULET_CHAOS_TEMPLE
Teleport.LLETYA
Teleport.RADAS_BLESSING_KOUREND_WOODLAND
Teleport.CABBAGE_PATCH_TELEPORT
Teleport.TAVERLEY_TELEPORT_TAB
Teleport.YANILLE_TELEPORT_TAB
Teleport.CONSTRUCTION_CAPE_HOSIDIUS
Teleport.CONSTRUCTION_CAPE_POLLNIVNEACH
Teleport.SLAYER_RING_GNOME_STRONGHOLD
Teleport.FENKENSTRAINS_CASTLE_TAB
Teleport.DRAYNOR_MANOR_TAB
Teleport.ROYAL_SEED_POD
Teleport.WATERBIRTH_TELEPORT_TAB
Teleport.FISHING_GUILD_TELEPORT_TAB
Teleport.FELDIP_HILLS_TELEPORT
Teleport.PEST_CONTROL_TELEPORT
Teleport.LUMBERYARD_TELEPORT
Teleport.WATSON_TELEPORT
Teleport.BURNING_AMULET_LAVA_MAZE
Teleport.XERICS_GLADE
Teleport.RADAS_BLESSING_MOUNT_KARUULM
Teleport.LEGENDS_GUILD_TELEPORT
Teleport.RELLEKKA_TELEPORT_TAB
Teleport.HOSIDIUS_TELEPORT_TAB
Teleport.CONSTRUCTION_CAPE_RIMMINGTON
Teleport.CONSTRUCTION_CAPE_YANILLE
Teleport.SLAYER_RING_RELLEKKA_CAVE
Teleport.BARROWS_TAB
Teleport.MIND_ALTAR_TAB
Teleport.DRAKANS_MEDALLION_VER_SINHAZA
Teleport.BARBARIAN_OUTPOST_TELEPORT_TAB
Teleport.CATHERBY_TELEPORT_TAB
```
</details>

These methods will later become available via the PowBot api, but in the DaxWalker library you can use these methods to manipulate the blacklist:

```
//Get a List<Teleport> of current blacklisted teleports
DaxWalker.getBlacklist()

//Remove the teleports provided via argument from the teleport blacklist
DaxWalker.removeBlacklistTeleports(Teleport... teleports)

//Add the teleports provided via argument to the teleport blacklist
DaxWalker.blacklistTeleports(Teleport...teleports)

//Remove all teleports from the teleport blacklist
DaxWalker.clearTeleportBlacklist()
```
