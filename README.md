#GriefPreventionFlags

GriefPrevention Flags is a plugin to allow admins to set flags for GriefPrevention claims. Either a single claim, or a global flag for all claims. It's similar to WorldGuard, but it's more performant, easier to use, and includes many more useful flags than WorldGuard does. It also doesn't require WorldEdit to run (unlike WorldGuard). Admins can also empower players to self-serve by giving them access to specific flags, which they can then only use on land claims they own.

Dependencies:
- GriefPrevention (Must Have)
- McMMo (Optional)


COMMANDS


SetClaimFlag
description: Sets a flag on a GriefPrevention land claim.
usage: /SetClaimFlag <flag> <optional parameters>
aliases: [setflag, placeflag, putflag, claimflag, placeclaimflag, putclaimflag]
permission: gpflags.setflag

UnsetClaimFlag
description: Un-sets (removes) a flag on a GriefPrevention land claim.
usage: /UnSetClaimFlag <flag>
aliases: [unsetflag, removeflag, unclaimflag, removeclaimflag]
permission: gpflags.unsetflag

SetDefaultClaimFlag
description: Sets a flag on all GriefPrevention land claims.
usage: /SetDefaultClaimFlag <flag> <optional parameters>
aliases: [setdefaultflag, placedefaultflag, putdefaultflag, defaultclaimflag, placedefaultclaimflag, putdefaultclaimflag]
permission: gpflags.defaultflags

UnsetDefaultClaimFlag
description: Un-sets (removes) a default flag from all GriefPrevention land claims.
usage: /UnSetClaimFlag <flag>
aliases: [unsetdefaultflag, removedefaultflag, removedefaultclaimflag]
permission: gpflags.defaultflags

ListClaimFlags
description: Lists all the flags currently set on a GriefPrevention land claim.
usage: /ListClaimFlags
aliases: [listclaimflags, claimflags, listflags]
permission: gpflags.listflags

SetWorldFlag
description: Sets a flag on an entire world.
usage: /SetWorldFlag <flag> <optional parameters>
aliases: [placeworldflag, putworldflag, worldflag]
permission: gpflags.setworldflag

UnsetWorldFlag
description: Un-sets (removes) a world-level flag.
usage: /UnSetWorldFlag <flag>
aliases: [unsetworldflag, removeworldflag, unworldflag]
permission: gpflags.unsetworldflag

SetServerFlag
description: Sets a flag everywhere.
usage: /SetServerFlag <flag> <optional parameters>
aliases: [placeserverflag, putserverflag, serverflag]
permission: gpflags.setserverflag

UnsetServerFlag
description: Un-sets (removes) a server-level flag.
usage: /UnSetServerFlag <flag>
aliases: [unsetserverflag, removeserverflag, unserverflag]
permission: gpflags.unsetservrflag

GPFReload
description: Reloads GriefPrevention Flags configuration settings from its config file.
usage: /GPFReload
permission: gpflags.reload


FLAGS
All of these can be set on the claim or per-world, using the commands above.


AllowPvP: 
Allows for PvP.

CommandWhiteList:
Whitelists command usable.

CommandBlackList:
Blacklists command usable.

EnterCommand:
Executes a console command when entering a claim.

EnterMessage:
Sends a message to the player when entering a claim.

ExitCommand:
Executes a console command when exiting a claim.

ExitMessage:
Sends a message to the player when exiting a claim.

HealthRegen:
Sets health regeneration values.

InfiniteArrows:
Disables arrow consumption on bows.

KeepInventory
Prevents losing items on death.

KeepLevel
Prevents losing levels on death.

NetherPortalConsoleCommand
Executes console command when entering a Portal.

NetherPortalPlayerCommand
Executes player command when entering a Portal.

NoChorusFruit
Disables chorus fruits teleportation.

NoCombatLoot
Clears drops on entity death.

NoEnderPearl
Disables ender pearls teleportation.

NoEnter
Disables entering claim for players without at least /accesstrust.

NoExpiration
Disables claim expiration.

NoFlight
Disables plugin flight in the claim.

NoFluidFlow
Disables fluid flows.

NoHunger
Disables hunger decay.

NoItemDrop
Disables dropping items.

NoItemPickup
Disables picking up items.

NoLeafDecay
Disables leaf decay.

NoLootProtection
Disables loot protection on player death.

NoMcMMODeathPenalty
Disables McMMO death penalty.

NoMcMMOSkills
Disables McMMO experience gaining in the claim.

NoMobDamage
Disables mob damage.

NoMobSpawns
Disables mob spawns (Except Eggs and Monster Spawners).

NoMonsterSpawns
Disables monster spawns and removes mobs wandering in the claim.

NoPetDamage
Disables damaging of pets.

NoPlayerDamage
Disables damaging of players.

NoWeatherChange
Disables weather change.

RespawnLocation
Sets spawn location for the claim (Useful for PvP arenas).

SpleefArena
Complex flag to create spleef arenas.

TrappedDestination
Sets trapped destinationfor the claim (Useful for admin claims).


PERMISSIONS


gpflags.admin.*:
description: Grants all administrative functionality.

gpflags.setflag:
description: Grants permission to use /SetClaimFlag. (Does not actually give the permission to set any flag, just use the command)
default: op

gpflags.unsetflag:
description: Grants permission to use /UnSetClaimFlag.
default: op

gpflags.setworldflag:
description: Grants permission to use /SetWorldFlag.
default: op

gpflags.unsetworldflag:
description: Grants permission to use /UnSetWorldFlag.
default: op

gpflags.setserverflag:
description: Grants permission to use /SetServerFlag.
default: op

gpflags.unsetserverflag:
description: Grants permission to use /UnSetServerFlag.
default: op

gpflags.defaultflags:
description: Grants permission to use /SetDefaultClaimFlag and /UnSetDefaultClaimFlag.
default: op

gpflags.listflags:
description: Grants permission to use /ListClaimFlags.
default: op

gpflags.reload:
description: Grants permission to use /GPFReload.
default: op

gpflags.allflags:
description: Grants permission to apply/remove all flags.
default: op

gpflags.bypass:
description: Makes a player immune to the limitations of flags like CommandBlackList, NoEnter, and NoFlight.
default: op

gpflags.<FLAGNAME>:
description: Gives to the user the permission to set a specific flag, for example gpflags.ExitMessage allows users to do /SetClaimFlag ExitMessage if they have the permission to set flags.
default: op