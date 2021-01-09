package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.WorldSettings;
import me.ryanhamshire.GPFlags.WorldSettingsManager;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

abstract class BaseCmd {

    final GPFlags PLUGIN;
    String command = null;
    String usage = "&cUnknown command";
    boolean requirePlayer = false;

    static FlagManager FLAG_MANAGER;
    static WorldSettingsManager WORLD_SETTINGS_MANAGER;

    BaseCmd(GPFlags plugin) {
        this.PLUGIN = plugin;
        FLAG_MANAGER = plugin.getFlagManager();
        WORLD_SETTINGS_MANAGER = plugin.getWorldSettingsManager();
    }

    abstract boolean execute(CommandSender sender, String[] args);

    List<String> tab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    String getUsage() {
        String usage = this.usage.replace("<", "&r<&b").replace(">", "&r>&7");
        return String.format("&6Command usage: &3&l/gpflags &b%s &7%s", command, usage);
    }

    static MessageSpecifier getFlagDefsMessage(Permissible player) {
        StringBuilder flagDefsList = new StringBuilder();
        Collection<FlagDefinition> defs = FLAG_MANAGER.getFlagDefinitions();
        flagDefsList.append("&b");
        for (FlagDefinition def : defs) {
            if (playerHasPermissionForFlag(def, player)) {
                flagDefsList.append(def.getName()).append("&7,&b ");
            }
        }
        String def = flagDefsList.toString();
        if (def.length() > 5) {
            def = def.substring(0, def.length() - 4);
        }
        return new MessageSpecifier(Messages.InvalidFlagDefName, def);
    }

    static boolean playerHasPermissionForFlag(FlagDefinition flagDef, Permissible player) {
        String flagName = flagDef.getName().toLowerCase(Locale.ROOT);
        return player == null || player.hasPermission("gpflags.flag.allflags") ||
                player.hasPermission("gpflags.flag." + flagName) ||

                // TODO (dec 25) remove "gpflags.(flag)" permission, use "gpflags.flag.(flag)" instead
                player.hasPermission("gpflags.allflags") ||
                player.hasPermission("gpflags." + flagName);
    }

    static boolean playerHasPermissionForEntity(Permissible player, String entityType) {
        // TODO (dec 25) remove "gpflags.(flag)" permission, use "gpflags.flag.(flag)" instead
        return player.hasPermission("gpflags.nomobspawnstype." + entityType) ||
                player.hasPermission("gpflags.flag.nomobspawnstype" + entityType);
    }

    static List<String> paramTab(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "nomobspawnstype":
                List<String> entityTypes = new ArrayList<>();
                for (EntityType entityType : EntityType.values()) {
                    String type = entityType.toString();
                    if (playerHasPermissionForEntity(sender, type.toLowerCase(Locale.ROOT))) {
                        String arg = args[1];
                        if (arg.contains(";")) {
                            if (arg.charAt(arg.length() - 1) != ';') {
                                arg = arg.substring(0, arg.lastIndexOf(';') + 1);
                            }
                            entityTypes.add(arg + type);
                        } else {
                            entityTypes.add(type);
                        }
                    }
                }
                return StringUtil.copyPartialMatches(args[1], entityTypes, new ArrayList<>());
            case "changebiome":
                ArrayList<String> biomes = new ArrayList<>();
                WorldSettings worldSettings = null;
                if (sender instanceof Player) {
                    worldSettings = WORLD_SETTINGS_MANAGER.get(((Player) sender).getWorld());
                }
                for (Biome biome : Biome.values()) {
                    if ((worldSettings != null && !(worldSettings.biomeBlackList.contains(biome.toString()))) || sender.hasPermission("gpflags.bypass")) {
                        biomes.add(biome.toString());
                    }
                }
                biomes.sort(String.CASE_INSENSITIVE_ORDER);
                return StringUtil.copyPartialMatches(args[1], biomes, new ArrayList<>());
            case "noopendoors":
                if (args.length != 2) return null;
                List<String> doorType = Arrays.asList("doors", "trapdoors", "gates");
                return StringUtil.copyPartialMatches(args[1], doorType, new ArrayList<>());
        }
        return Collections.singletonList("[<parameters>]"); // TODO get from flag?!?!
    }

    static List<String> flagTab(CommandSender sender, String arg) {
        List<String> flags = new ArrayList<>();
        FLAG_MANAGER.getFlagDefinitions().forEach(flagDefinition -> {
            if (playerHasPermissionForFlag(flagDefinition, sender)) {
                flags.add(flagDefinition.getName());
            }
        });
        return StringUtil.copyPartialMatches(arg, flags, new ArrayList<>());
    }

}
