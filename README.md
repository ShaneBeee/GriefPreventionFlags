# GriefPreventionFlags

I created this Minecraft server plugin using the Bukkit API to empower server admins to create unique experiences for players based on location in the world.  It's similar to WorldGuard, but it's more performant, easier to use, and includes many more useful flags than WorldGuard does.  It also doesn't require WorldEdit to run (unlike WorldGuard).  Admins can also empower players to self-serve by giving them access to specific flags, which they can then only use on land claims they own.

On my own servers, I used it to create PvP arenas, parkour challenges, a prison, a spleef arena, a snowball fight arena, fast travel teleporters, and a new player spawn where players never die or get stuck, and can't injure the animals or NPCs.

I stopped working on this project after Minecraft 1.10.  Anyone may use my source code to continue the project, but see the LICENSE file for restrictions.



Adds WorldGuard-style flags to GriefPrevention land claims, worlds, and servers! Designate PvP arenas, prevent monsters from spawning, add enter/exit messages, make nether portals run console commands, and more!
![gpflagsBanner](https://s23.postimg.org/3ml0fxquz/gpflags_Banner.png) 
​
This plugin works very well for Minecraft 1.10, and some earlier versions are available for older versions of Minecraft. However, it will not be updated for future Minecraft versions because I've finally retired from Minecraft development (5 years of plugins!), and I'm moving on to develop indie games. :) You can follow me in my adventures here: 

facebook.com/BigScaryGames

**Features**

Doesn't require WorldEdit.

Very easy to use.
GPFlags uses very simplified slash commands. Just stand in the land claim where you want to modify flags, then use easy commands. Suppose you wanted to disable monster spawning in your spawn area...

With WorldGuard:
/region flag myregionname mob-spawning deny

With GPFlags:
/setflag NoMonsterSpawns

The WorldGuard approach also assumes you named your region, and remember its name. ;)

Performant.
GPFlags doesn't waste CPU cycles on flag types which aren't in use, and all flags are designed to minimize compute time.

Easily set flags as "default on" for all land claims.
This should make it easy, for example, to stop monsters from spawning on all player land claims including those claims which haven't been created yet. You can make exceptions for specific land claims or subdivisions.

Easily set flags for entire worlds, or even for the entire server.
You're not limited to flagging claimed areas. You can even use the inversion feature to apply a flag to everywhere EXCEPT for all land claims, or everywhere except for specific worlds, or specific worlds except for land claims in those worlds, or specific worlds except for specific land claims, or everywhere in the server except for specific land claims... it's very flexible.

Empower your players to self-serve.
Optionally give players access to specific flags (or all flags) via permissions. Players may only set flags in claims they own, so no worries about a player placing a flag in another player's area. You might for example allow players to set their own enter/exit messages, disable monster spawns in their claims, or completely deny claim access to anyone who doesn't have their permission.

Created by an experienced and professional developer.
This is a quality plugin that delivers what its description promises. I have a great track record for creating quality plugins and maintaining them over time.

Messages are customizable, even into other languages.
Use messages.yml to translate messages players receive into any other language.

The YAML file format isn't always easy to work with, so please make a backup of your messages.yml file before reloading your server with message changes. Also, it helps to use a YAML editor like Notepad++, which can point out syntax errors while you edit.

Available Flags

NoMonsterSpawns - Disables all monster spawns except for dungeon spawners and spawn eggs (both of which GriefPrevention limits to players with build permission in land claims). Also removes monsters which spawn outside the flagged area and then wander in.

NoMobSpawns - Prevents ALL mobs from spawning (not just monsters) in an area. Same exceptions as for the NoMonsterSpawns flag above.

AllowPvP - This is the flag for creating PvP arenas, where players can fight. This flag overrides GriefPrevention's PvP protections in land claims where you set it, allowing players who are standing in an AllowPvP area to damage other players. If you want to also disable PvP in the wilderness (outside of land claims), you can do that in the GriefPreventionFlags config file (it's a per-world setting).

EnterMessage / ExitMessage - Sends a message to players when they enter or exit (respectively) a land claim. These messages support formatting codes using the dollar sign ($). Example usage: /SetFlag EnterMessage $1Welcome to %owner%'s house!

NetherPortalConsoleCommand - Runs one or more console commands when a player steps through a nether portal in the flagged area. Use %name% or %uuid% placeholders to target the player stepping through the portal, and separate multiple command lines with semicolons [;]. If your in-game command entry box is too short for all your commands, consider backing-up your flags.yml file and then modifying it with a text editor to get more command lines in for a single portal, then using /GPFReload to load your edited file. Example usage: /SetFlag NetherPortalConsoleCommand tp %name% 0, 65, 0;xp 10L %name%

EnterCommand - Executes one or more console commands when a player enters the flagged area, similar to NetherPortalConsoleCommand.

ExitCommand - Executes one or more console commands when a player exits the flagged area, similar to NetherPortalConsoleCommand.

NoEnter - Requires players to have /AccessTrust or higher permission in a land claim (or the gpflags.bypass permission node) before they may move into a flagged area. This flag requires an explanatory message, which will be sent to any rejected player, and is only effective when applied to land claims (not worlds). Example usage: /SetFlag NoEnter You don't have the %owner%'s permission to enter this area.

CommandBlackList - Prevents players from using any commands you list in the flagged area, unless they have the gpflags.bypass permission. Example usage: /SetFlag CommandBlackList sethome;setwarp;kit

CommandWhiteList - flag prevents players from using any commands except those you list in the flagged area, unless they have the gpflags.bypass permission. Example usage: /SetFlag CommandWhiteList tell;me;home

HealthRegen - Regenerates the amount of health you specify every 5 seconds for any players in the flagged area. To give you a sense of scale when setting this flag's regeneration rate, default max player health is 20. Example usage: /SetFlag HealthRegen 5

NoHunger - Prevents damage from starvation and food level loss for all players inside the flagged area. (Food level may be momentarily lost, but will be restored to its previous value shortly). Optionally, you may specify an amount of food level to be regenerated per 5 seconds. For example: /SetFlag NoHunger 5 would regenerate 5 food levels per five seconds (max food level is 20).

NoMobDamage - Disables non-player damage to passive mobs and named aggressive mobs. Player damage is already limited by GriefPrevention (/ContainerTrust for animals, /Trust for villagers, but named monsters are never protected). One use of this flag might be to protect your spawn animals/villagers from monsters and clever players who might push them into lava, off a cliff, or under a waterfall.

NoPetDamage - cancels ALL damage to pets in flagged areas, even direct damage from their owners. A "pet" is any tameable entity with an owner, like a dog, cat, or horse for example.

RespawnLocation - Overrides the usual respawn rules to respawn the player in a specific location who dies in the flagged area. For example, consider respawning a player at the beginning of a parkour challenge or just outside a pvp arena. You may optionally specify pitch and yaw (facing direction) as well. Example usages: /SetFlag RespawnLocation world 112.5 68 265.5 or /SetFlag RespawnLocation world 112.5 68 265.5 90 45

KeepInventory - If a player dies in an area where this flag is active, he will keep his inventory instead of dropping it on the ground.

InfiniteArrows - When an arrow lands in an area where this flag is active, the arrow will be sent back to the firing player's inventory. This includes arrows which hit live targets like monsters and other players, and they will still deal damage. This cannot be used to duplicate arrows using an infinity-enchanted bow. A great option for shooting ranges and combat arenas!

KeepLevel - When a player dies in an area where KeepLevel is active, he or she will not lose (or drop) any experience.

NetherPortalPlayerCommand - Causes any players who walk into a nether portal in the area where the flag is applied to automatically run a command line instead of teleporting (it runs as the player, not as a console command). Helpful to give players a /home portal or random wilderness teleport portal, for example.

NoCombatLoot - When a mob (except for players) dies in an area with this flag active, no loot will drop. Using this, you can create combat challenges where players can keep their inventories and experience (with other flags above), but prevent players from abusing those flags to farm loot. Player death loot is controlled by the above KeepInventory flag.

NoPlayerDamage - Prevents ALL damage to players in areas where it's set. Think about new player starting areas, non-lethal parkour challenges, and secrets hidden underneath presumably lethal lava pools! :)

NoFluidFlow - Prevents source fluid (water, lava) blocks from spreading in the flagged area. Note that GriefPrevention already prevents fluids from flowing into land claims from outside.

NoFlight - Prevents players from legitimately flying (this is not a no-cheating flag, see other plugins to fight hacked clients) in the flagged area. Invincible players will fall when entering the area, while others will be teleported down to the first non-air block beneath them.

NoWeatherChange - prevents weather from changing in a world, even by operators using commands. If you change your mind about the weather in a world, you have to first disable the flag, then change the weather, then re-enable the flag. You should use this only with /setserverflag or /setworldflag, because it has no effect on individual land claims or subdivisions. Note: If you lock the weather during a thunderstorm, it will never end. If you lock the weather when there is no thunder, a storm will never come.

TrappedDestination - Allows players to use GriefPrevention's /trapped command in administrative land claims by specifying where the player will go when he gets "unstuck". Ordinarily, administrative land claims don't allow players to use the command at all.

NoLootProtection - Disables GriefPrevention's player death loot "anti-theft" feature in the flagged area, allowing any player to pick up the items a player drops when he or she dies in that area. Useful for competitive areas where loot can be a reward, like PvP arenas.

NoMcMMOSkills - Prevents mcMMO skill usage (activated skills, secondary skills, disarms, etc) in the flagged area. You might use this to create PvE challenge areas or specialized PvP arenas where mcMMO won't give some players an advantage over others.

NoMcMMODeathPenalty - cancels McMMODeathPenalties when a player dies in a flagged area.

NoLeafDecay - Prevents leaves in the flagged area from disappearing when the logs holding them up are removed.

NoItemDrop - Prevents players from dropping items in a flagged area.

NoChorusFruit - Prevents players from teleporting from or to a flagged area using chorus fruit.

SpleefArena - Completely automates a Spleef minigame (players compete to remove blocks out from under each other until someone falls) in the flagged area. Example usage: /SetFlag SpleefArena 35:6 45 20

The above example will generate a pink wool block (id:data = 35:6) 20 blocks above every bricks block (id = 45) in the flagged area every time a player dies in the flagged area. It will also allow ONLY pink wool blocks to be broken by any player even without build permission, and won't drop those blocks as items when they're broken.

To set up a spleef arena, first flag the claim or subdivision as shown above. Then dig down underneath where the breakable arena surface (pink wool in the above example) will be and use your marker blocks (bricks in the above example) to indicate the shape of your arena, which does NOT have to be flat, rectangular, or single-block thick. The y offset (the last flag parameter) dictates how far down you have to place the marker blocks from where you want the arena surface to generate. To test your settings, use the Vanilla /kill command while standing in the flagged area. Your death will trigger the arena surface to be built per your specifications.

Use the discussion tab to tell me which flags you want to see added the most! :)

Limitations vs. WorldGuard

If you're considering replacing WorldGuard, please review this first. For most servers, these won't be important limitations. :) However, I ask that you review these before you buy. There is no reason you can't continue to use WorldGuard alongside GriefPrevention Flags, to get the best of both.

Land claims do not overlap or stack vertically, and always reach to max build height. These are GriefPrevention limitations which make land claims easy to manage. If for example you wanted to disable PvP in an area which already includes some land claims, you would have to either convert those land claims into subdivisions of a larger land claim and set the flag on that big claim, OR use a WorldGuard region with passthrough set for all players.

Some of WorldGuard's lesser-used flags are not supported by GPFlags simply because they're not needed. If you need one of them, please post in the discussion tab and I will happily add it.

Situational limitations aside, GPFlags includes many very powerful and flexible flags WorldGuard does not (see above), and it's also much more performant on the whole - even when you add GriefPrevention's CPU usage, and don't penalize WorldGuard for -required- WorldEdit's CPU usage!

**Installation**

Copy the JAR file into your server's plugins folder and then execute the /reload command (or reboot the server). Easy! :)

**Commands** 

SetFlag <Flag> <Parameters> - Sets a flag on the GriefPrevention land claim you're currently in. Most flags don't require any parameters.

UnSetFlag <Flag> - Removes a flag from the GriefPrevention land claim you're currently in.

SetWorldFlag <Flag> <Parameters> - Sets a flag on the world you're currently in. Most flags don't require any parameters.

UnSetWorldFlag <Flag> - Removes a flag from the world you're currently in.

SetServerFlag <Flag> <Parameters> - Sets a flag across the entire server. Most flags don't require any parameters.

UnSetServerFlag <Flag> - Removes a server-level flag.

ListFlags - Lists all the flags set on the GriefPrevention land claim you're currently in.

SetDefaultFlag <Flag> <Parameters> - Sets a flag on all claims except for those you explicitly exempt with /UnSetFlag.

UnSetDefaultFlag <Flag> - Reverses /SetDefaultFlag.

GPFReload - Reloads configuration settings from the GPFlags config file.

**Permissions**

gpflags.admin.* - Sets all of the below, for your convenience.
gpflags.setflag
gpflags.unsetflag
gpflags.setworldflag
gpflags.unsetworldflag
gpflags.setserverflag
gpflags.unsetserverflag
gpflags.listflags
gpflags.defaultflags
gpflags.reload
gpflags.allflags - Grants permission to place/remove all flags.
gpflags.bypass - Makes a player immune to limiting flags like NoEnter and CommandBlackList.

To grant permissions to use only specific flags, use the flag name. For example, gpflags.nomonsterspawns. Changing the flags on a land claim requires the player making the change to have EDIT permission on the land claim. Generally, this means the claim creator only or administrators in the case of administrative land claims.

Editing another player's land claim requires Grief Prevention's griefprevention.deleteclaims permission.
