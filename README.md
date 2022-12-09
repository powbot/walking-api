# walking-api

Many teleports are blacklisted by default to aid in reducing computation time of each call.  With those teleports blacklisted, one can expect a call to take roughly .7-1 sec to generate player details, send the request, and receive a response.  I took care to only blacklist teleports that are used in specific scenarios, and leave the generally accessible ones available.  
<details>
<summary>Here are the blacklisted teleports</summary>

```
Teleport.BARBARIAN_OUTPOST_TELEPORT_TAB
Teleport.BARROWS_TAB
Teleport.BATTLEFRONT_TAB
Teleport.BURNING_AMULET_BANDIT_CAMP
Teleport.BURNING_AMULET_CHAOS_TEMPLE
Teleport.BURNING_AMULET_LAVA_MAZE
Teleport.CABBAGE_PATCH_TELEPORT
Teleport.CATHERBY_TELEPORT_TAB
Teleport.CONSTRUCTION_CAPE_BRIMHAVEN
Teleport.CONSTRUCTION_CAPE_HOSIDIUS
Teleport.CONSTRUCTION_CAPE_POLLNIVNEACH
Teleport.CONSTRUCTION_CAPE_RIMMINGTON
Teleport.CONSTRUCTION_CAPE_TAVERLEY
Teleport.CONSTRUCTION_CAPE_YANILLE
Teleport.CRAFTING_CAPE_TELEPORT
Teleport.DIGSITE_TELEPORT
Teleport.DRAKANS_MEDALLION_VER_SINHAZA
Teleport.DRAYNOR_MANOR_TAB
Teleport.ECTOPHIAL
Teleport.ELF_CAMP_TELEPORT
Teleport.FARMING_CAPE_TELEPORT
Teleport.FELDIP_HILLS_TELEPORT
Teleport.FENKENSTRAINS_CASTLE_TAB
Teleport.FISHING_GUILD_TELEPORT_TAB
Teleport.HOSIDIUS_TELEPORT_TAB
Teleport.KEY_MASTER_TELEPORT
Teleport.KHAZARD_TELEPORT_TAB
Teleport.LEGENDS_GUILD_TELEPORT
Teleport.LLETYA
Teleport.LUMBERYARD_TELEPORT
Teleport.LUNAR_ISLE_TELEPORT
Teleport.MIND_ALTAR_TAB
Teleport.MORTTON_TELEPORT
Teleport.MOS_LE_HARMLESS_TELEPORT
Teleport.NARDAH_TELEPORT
Teleport.OURANIA_TELEPORT_TAB
Teleport.PEST_CONTROL_TELEPORT
Teleport.POLLNIVNEACH_TELEPORT_TAB
Teleport.RADAS_BLESSING_KOUREND_WOODLAND
Teleport.RADAS_BLESSING_MOUNT_KARUULM
Teleport.RELLEKKA_TELEPORT_TAB
Teleport.REVENANT_CAVES_TELEPORT
Teleport.RIMMINGTON_TELEPORT_TAB
Teleport.ROYAL_SEED_POD
Teleport.SALVE_GRAVEYARD_TAB
Teleport.SLAYER_RING_GNOME_STRONGHOLD
Teleport.SLAYER_RING_MORYTANIA
Teleport.SLAYER_RING_RELLEKKA_CAVE
Teleport.TAVERLEY_TELEPORT_TAB
Teleport.WATERBIRTH_TELEPORT_TAB
Teleport.WATSON_TELEPORT
Teleport.WEST_ARDOUGNE_TELEPORT_TAB
Teleport.XERICS_GLADE
Teleport.YANILLE_TELEPORT_TAB
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
