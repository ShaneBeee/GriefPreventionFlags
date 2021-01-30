package me.ryanhamshire.GPFlags;

import com.google.common.io.Files;
import me.ryanhamshire.GPFlags.flags.FlagDef_AllowPvP;
import me.ryanhamshire.GPFlags.flags.FlagDef_ChangeBiome;
import me.ryanhamshire.GPFlags.flags.FlagDef_CommandBlackList;
import me.ryanhamshire.GPFlags.flags.FlagDef_CommandWhiteList;
import me.ryanhamshire.GPFlags.flags.FlagDef_EnterCommand;
import me.ryanhamshire.GPFlags.flags.FlagDef_EnterCommand_Members;
import me.ryanhamshire.GPFlags.flags.FlagDef_EnterCommand_Owner;
import me.ryanhamshire.GPFlags.flags.FlagDef_EnterMessage;
import me.ryanhamshire.GPFlags.flags.FlagDef_EnterPlayerCommand;
import me.ryanhamshire.GPFlags.flags.FlagDef_ExitCommand;
import me.ryanhamshire.GPFlags.flags.FlagDef_ExitCommand_Members;
import me.ryanhamshire.GPFlags.flags.FlagDef_ExitCommand_Owner;
import me.ryanhamshire.GPFlags.flags.FlagDef_ExitMessage;
import me.ryanhamshire.GPFlags.flags.FlagDef_ExitPlayerCommand;
import me.ryanhamshire.GPFlags.flags.FlagDef_HealthRegen;
import me.ryanhamshire.GPFlags.flags.FlagDef_InfiniteArrows;
import me.ryanhamshire.GPFlags.flags.FlagDef_KeepInventory;
import me.ryanhamshire.GPFlags.flags.FlagDef_KeepLevel;
import me.ryanhamshire.GPFlags.flags.FlagDef_NetherPortalConsoleCommand;
import me.ryanhamshire.GPFlags.flags.FlagDef_NetherPortalPlayerCommand;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoChorusFruit;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoCombatLoot;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoElytra;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoEnderPearl;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoEnter;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoEnterPlayer;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoExpiration;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoExplosionDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoFallDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoFireDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoFireSpread;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoFlight;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoFluidFlow;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoGrowth;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoHunger;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoIceForm;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoItemDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoItemDrop;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoItemPickup;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoLeafDecay;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoLootProtection;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMcMMODeathPenalty;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMcMMOSkills;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMcMMOXP;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMobDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMobSpawns;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMobSpawnsType;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMonsterSpawns;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoMonsters;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoOpenDoors;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoPetDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoPlayerDamage;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoPlayerDamageByMonster;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoSnowForm;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoVehicle;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoVineGrowth;
import me.ryanhamshire.GPFlags.flags.FlagDef_NoWeatherChange;
import me.ryanhamshire.GPFlags.flags.FlagDef_OwnerFly;
import me.ryanhamshire.GPFlags.flags.FlagDef_OwnerMemberFly;
import me.ryanhamshire.GPFlags.flags.FlagDef_PlayerGamemode;
import me.ryanhamshire.GPFlags.flags.FlagDef_PlayerTime;
import me.ryanhamshire.GPFlags.flags.FlagDef_PlayerWeather;
import me.ryanhamshire.GPFlags.flags.FlagDef_ProtectNamedMobs;
import me.ryanhamshire.GPFlags.flags.FlagDef_RaidMemberOnly;
import me.ryanhamshire.GPFlags.flags.FlagDef_RespawnLocation;
import me.ryanhamshire.GPFlags.flags.FlagDef_SpleefArena;
import me.ryanhamshire.GPFlags.flags.FlagDef_TrappedDestination;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GPFlagsConfig {
    
    private final GPFlags plugin;
    private final FlagManager flagManager;

    public static boolean LOG_ENTER_EXIT_COMMANDS = true;

    public GPFlagsConfig(GPFlags plugin) {
        this.plugin = plugin;
        this.flagManager = plugin.getFlagManager();
        loadConfig();
    }

    public void loadConfig() {
        this.flagManager.clear();

        //load the config if it exists
        FileConfiguration inConfig = YamlConfiguration.loadConfiguration(new File(FlagsDataStore.configFilePath));
        FileConfiguration outConfig = new YamlConfiguration();

        List<World> worlds = plugin.getServer().getWorlds();
        ArrayList<String> worldSettingsKeys = new ArrayList<>();
        for (World world : worlds) {
            worldSettingsKeys.add(world.getName());
        }

        for (String worldName : worldSettingsKeys) {
            WorldSettings settings = plugin.getWorldSettingsManager().create(worldName);

            LOG_ENTER_EXIT_COMMANDS = inConfig.getBoolean("Settings.Log Enter/Exit Messages To Console", true);
            outConfig.set("Settings.Log Enter/Exit Messages To Console", LOG_ENTER_EXIT_COMMANDS);

            settings.worldGamemodeDefault = inConfig.getString("World Flags." + worldName + ".Default Gamemode", "survival");
            String worldGMDefault = settings.worldGamemodeDefault;
            if (worldGMDefault == null || !worldGMDefault.equalsIgnoreCase("survival") && !worldGMDefault.equalsIgnoreCase("creative") &&
                    !worldGMDefault.equalsIgnoreCase("adventure") && !worldGMDefault.equalsIgnoreCase("spectator")) {
                settings.worldGamemodeDefault = "survival";
            }
            outConfig.set("World Flags." + worldName + ".Default Gamemode", settings.worldGamemodeDefault);

            settings.pvpRequiresClaimFlag = inConfig.getBoolean("World Flags." + worldName + ".PvP Only In PvP-Flagged Claims", false);
            outConfig.set("World Flags." + worldName + ".PvP Only In PvP-Flagged Claims", settings.pvpRequiresClaimFlag);

            settings.pvpDeniedMessage = inConfig.getString("World Flags." + worldName + ".Message To Send When PvP Is Denied",
                    "Player vs. player combat is restricted to designated areas.");
            outConfig.set("World Flags." + worldName + ".Message To Send When PvP Is Denied", settings.pvpDeniedMessage);

            settings.pvpEnterClaimMessageEnabled = inConfig.getBoolean("World Flags." + worldName + ".Send Message On Enter PvP Enabled Claim", false);
            outConfig.set("World Flags." + worldName + ".Send Message On Enter PvP Enabled Claim", settings.pvpEnterClaimMessageEnabled);

            settings.pvpEnterClaimMessage = inConfig.getString("World Flags." + worldName + ".Message",
                    "PvP is enabled in this claim, be careful");
            outConfig.set("World Flags." + worldName + ".Message", settings.pvpEnterClaimMessage);

            settings.pvpExitClaimMessageEnabled = inConfig.getBoolean("World Flags." + worldName + ".Send Message On Exit PvP Enabled Claim", false);
            outConfig.set("World Flags." + worldName + ".Send Message On Exit PvP Enabled Claim", settings.pvpExitClaimMessageEnabled);

            settings.pvpExitClaimMessage = inConfig.getString("World Flags." + worldName + ".ExitMessage", "PvP is disabled in this area, you are now safe");
            outConfig.set("World Flags." + worldName + ".ExitMessage", settings.pvpExitClaimMessage);

            // Adds default biomes to be ignored in the ChangeBiome flag
            settings.biomeBlackList = inConfig.getList("World Flags." + worldName + ".Biomes.Blacklist", Arrays.asList("MUSHROOM_FIELDS", "MUSHROOM_FIELD_SHORE"));
            outConfig.set("World Flags." + worldName + ".Biomes.Blacklist", settings.biomeBlackList);

            settings.noMonsterSpawnIgnoreSpawners = inConfig.getBoolean("World Flags." + worldName + ".NoMonsterSpawn Flag Ignores Spawners and Eggs", true);
            outConfig.set("World Flags." + worldName + ".NoMonsterSpawn Flag Ignores Spawners and Eggs", settings.noMonsterSpawnIgnoreSpawners);

            outConfig.options().header("GriefPrevention Flags\n" + "Plugin Version: " + plugin.getDescription().getVersion() +
                    "\nServer Version: " + plugin.getServer().getVersion() + "\n\n");
        }

        try {
            outConfig.save(FlagsDataStore.configFilePath);
            Util.log("Finished loading configuration.");
        } catch (IOException exception) {
            Util.log("Unable to write to the configuration file at \"" + FlagsDataStore.configFilePath + "\"");
        }

        //register flag definitions
        if (!plugin.registeredFlagDefinitions) {
            plugin.registeredFlagDefinitions = true;
            this.flagManager.registerFlagDefinition(new FlagDef_NoMonsterSpawns(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoMonsters(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_AllowPvP(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_EnterMessage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_ExitMessage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_EnterCommand(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_EnterPlayerCommand(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_ExitCommand(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_ExitPlayerCommand(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_RespawnLocation(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_KeepInventory(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_InfiniteArrows(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_KeepLevel(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NetherPortalPlayerCommand(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NetherPortalConsoleCommand(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoCombatLoot(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoMobSpawns(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoPlayerDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoPlayerDamageByMonster(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoEnter(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoMobDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoFluidFlow(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_HealthRegen(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoHunger(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_CommandWhiteList(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_CommandBlackList(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoFlight(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_TrappedDestination(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoLootProtection(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoEnderPearl(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoExpiration(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoLeafDecay(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoPetDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoWeatherChange(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoItemPickup(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoChorusFruit(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_SpleefArena(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoItemDrop(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoGrowth(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_OwnerFly(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_OwnerMemberFly(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoEnterPlayer(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_PlayerWeather(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_PlayerTime(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_PlayerGamemode(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoVineGrowth(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoSnowForm(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoIceForm(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoFireSpread(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoFireDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoFallDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_EnterCommand_Owner(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_EnterCommand_Members(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_ExitCommand_Owner(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_ExitCommand_Members(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoExplosionDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_ProtectNamedMobs(this.flagManager, plugin));

            this.flagManager.registerFlagDefinition(new FlagDef_ChangeBiome(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoOpenDoors(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoVehicle(this.flagManager, plugin));

            this.flagManager.registerFlagDefinition(new FlagDef_NoMobSpawnsType(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoItemDamage(this.flagManager, plugin));
            this.flagManager.registerFlagDefinition(new FlagDef_NoElytra(this.flagManager, plugin));

            try {
                Class.forName("org.bukkit.event.raid.RaidTriggerEvent");
                this.flagManager.registerFlagDefinition(new FlagDef_RaidMemberOnly(this.flagManager, plugin));
            } catch (ClassNotFoundException e) {
                if (Util.isRunningMinecraft(1, 14)) {
                    Util.log("&cRaidEvent classes not found:");
                    Util.log("&7  - Update to latest Spigot build for raid flag to work");
                }
            }

            //try to hook into mcMMO
            try {
                if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
                    this.flagManager.registerFlagDefinition(new FlagDef_NoMcMMOSkills(this.flagManager, plugin));
                    this.flagManager.registerFlagDefinition(new FlagDef_NoMcMMODeathPenalty(this.flagManager, plugin));
                    // Experimental
                    this.flagManager.registerFlagDefinition(new FlagDef_NoMcMMOXP(this.flagManager, plugin));
                }
            }
            //if failed, we just won't have those flags available
            catch (NoClassDefFoundError ignore) {
            }
        } else {
            // Update world settings for flags (probably on a reload)
            this.flagManager.getFlagDefinitions().forEach(flagDefinition -> flagDefinition.updateSettings(plugin.getWorldSettingsManager()));
        }

        try {
            File flagsFile = new File(FlagsDataStore.flagsFilePath);
            List<MessageSpecifier> errors = this.flagManager.load(flagsFile);
            if (errors.size() > 0) {
                File errorFile = new File(FlagsDataStore.flagsErrorFilePath);
                //noinspection UnstableApiUsage
                Files.copy(flagsFile, errorFile);
                for (MessageSpecifier error : errors) {
                    Util.log("Load Error: " + plugin.getFlagsDataStore().getMessage(error.messageID, error.messageParams));
                }
                Util.log("Problems encountered reading the flags data file! " +
                        "Please share this log and your 'flagsError.yml' file with the developer.");
            }
        } catch (Exception e) {
            Util.log("Unable to initialize the file system data store.  Details:");
            Util.log(e.getMessage());
            e.printStackTrace();
        }

        //drop any flags which no longer correspond to existing land claims (maybe they were deleted)
        Collection<Claim> topLevelClaims = GriefPrevention.instance.dataStore.getClaims();
        HashSet<String> validIDs = new HashSet<>();
        for (Claim claim : topLevelClaims) {
            validIDs.add(claim.getID().toString());
            for (Claim subclaim : claim.children) {
                validIDs.add(subclaim.getID().toString());
            }
        }
        this.flagManager.removeExceptClaimIDs(validIDs);
        Util.log("Finished loading data.");
    }
    
}
