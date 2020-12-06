package me.ryanhamshire.GPFlags;

import com.google.common.collect.ImmutableList;
import me.ryanhamshire.GPFlags.flags.FlagDef_ChangeBiome;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandHandler {

    private final GPFlags plugin;

    public CommandHandler(GPFlags plugin) {
        this.plugin = plugin;
    }

    private boolean playerHasPermissionForFlag(FlagDefinition flagDef, Permissible player) {
        String flagName = flagDef.getName();
        return player == null || player.hasPermission("gpflags.allflags") || player.hasPermission("gpflags." +
                flagName) || player.hasPermission("gpflags." + flagName.toLowerCase());
    }

    private MessageSpecifier getFlagDefsMessage(Permissible player) {
        StringBuilder flagDefsList = new StringBuilder();
        Collection<FlagDefinition> defs = plugin.getFlagManager().getFlagDefinitions();
        for (FlagDefinition def : defs) {
            if (this.playerHasPermissionForFlag(def, player)) {
                flagDefsList.append(def.getName()).append(" ");
            }
        }
        return new MessageSpecifier(Messages.InvalidFlagDefName, flagDefsList.toString());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (cmd.getName().equalsIgnoreCase("allflags")) {
            for (FlagDefinition flag : plugin.getFlagManager().getFlagDefinitions()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        flag.getName() + " &7" + flag.getFlagType()));
            }
            return true;
        }
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            if (!cmd.getLabel().contains("server") && !cmd.getName().equalsIgnoreCase("GPFReload") &&
                    !cmd.getName().equalsIgnoreCase("SetClaimFlagPlayer")) {
                plugin.getLogger().info(ChatColor.RED + "This command can only be issued by a player");
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("GPFReload")) {
            plugin.reloadConfig();
            GPFlags.sendMessage(player, TextMode.Success, Messages.ReloadComplete);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("SetDefaultClaimFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];

            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            SetFlagResult result = plugin.getFlagManager().setFlag(FlagManager.DEFAULT_FLAG_ID, def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.DefaultFlagSet);
                plugin.getFlagManager().save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetDefaultClaimFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            SetFlagResult result = plugin.getFlagManager().unSetFlag(FlagManager.DEFAULT_FLAG_ID, def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.DefaultFlagUnSet);
                plugin.getFlagManager().save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("SetServerFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.SERVER)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInServer);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            SetFlagResult result = plugin.getFlagManager().setFlag("everywhere", def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.ServerFlagSet);
                plugin.getFlagManager().save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetServerFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            SetFlagResult result = plugin.getFlagManager().unSetFlag("everywhere", def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.ServerFlagUnSet);
                plugin.getFlagManager().save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("UnsetClaimFlagPlayer")) {
            if (args.length < 2) return false;
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                GPFlags.sendMessage(sender, "&c" + args[0] + " &7is not online");
                return false;
            }
            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
            if (claim == null || claim.allowEdit(player) != null) {
                GPFlags.sendMessage(sender, "&cThis player is not standing in a claim they own");
                return false;
            }

            String flagName = args[1];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(sender, String.format("&c%s&7 is not a valid flag", flagName));
                return false;
            }
            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            SetFlagResult result = plugin.getFlagManager().unSetFlag(claim, def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(sender, color, result.message.messageID, result.message.messageParams);
            String message;
            if (result.success) {
                plugin.getFlagManager().save();
                message = String.format("&7Flag &b%s &7successfully unset in &b%s&7's claim.", def.getName(), player.getName());

            } else {
                message = String.format("&cFlag &b%s &cfailed to unset in &b%s&c's claim.", def.getName(), player.getName());
            }
            GPFlags.sendMessage(sender, message);

            return true;
        }

        // Set a claimFlag for a player from console
        if (cmd.getName().equalsIgnoreCase("SetClaimFlagPlayer")) {
            if (args.length < 2) return false;
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                GPFlags.sendMessage(sender, "&c" + args[0] + " &7is not online");
                return false;
            }
            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
            if (claim == null || claim.allowEdit(player) != null) {
                GPFlags.sendMessage(sender, "&cThis player is not standing in a claim they own");
                return false;
            }

            String flagName = args[1];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(sender, "&c" + args[1] + "&7 is not a valid flag");
                return false;
            }
            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            String[] params = new String[args.length - 2];
            for (int i = 2; i < args.length; i++) {
                params[i - 2] = args[i];
            }

            // SET BIOME
            if (flagName.equalsIgnoreCase("ChangeBiome")) {
                if (args.length < 3) return false;
                FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) plugin.getFlagManager().getFlagDefinitionByName("changebiome"));
                String biome = params[0].toUpperCase().replace(" ", "_");
                if (!flagD.changeBiome(sender, claim, biome)) return true;
            }

            SetFlagResult result = plugin.getFlagManager().setFlag(claim.getID().toString(), def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(sender, color, result.message.messageID, result.message.messageParams);
            if (result.success) {
                plugin.getFlagManager().save();
                GPFlags.sendMessage(sender, "&7Flag &b" + def.getName() + " &7successfully set in &b" + player.getName() + "&7's claim.");
                return true;
            }

            return true;
        }

        if (player == null) {
            GPFlags.addLogEntry("You must be logged into the game to use that command.");
        }

        if (cmd.getName().equalsIgnoreCase("SetWorldFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.WORLD)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInWorld);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            SetFlagResult result = plugin.getFlagManager().setFlag(player.getWorld().getName(), def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.WorldFlagSet);
                plugin.getFlagManager().save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetWorldFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            SetFlagResult result = plugin.getFlagManager().unSetFlag(player.getWorld().getName(), def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.WorldFlagUnSet);
                plugin.getFlagManager().save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("ListClaimFlags") && player != null) {
            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

            Collection<Flag> flags;
            boolean flagsFound = false;
            StringBuilder builder1 = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            StringBuilder builder3 = new StringBuilder();
            if (claim != null) {
                flags = plugin.getFlagManager().getFlags(claim.getID().toString());
                for (Flag flag : flags) {
                    flagsFound = true;
                    builder1.append((flag.getSet() ? "+" : "-") + flag.flagDefinition.getName()).append(" ");
                }

                if (claim.parent != null) {
                    flags = plugin.getFlagManager().getFlags(claim.parent.getID().toString());
                    for (Flag flag : flags) {
                        flagsFound = true;
                        builder2.append((flag.getSet() ? "+" : "-") + flag.flagDefinition.getName()).append(" ");
                    }
                }

                flags = plugin.getFlagManager().getFlags(FlagManager.DEFAULT_FLAG_ID);
                for (Flag flag2 : flags) {
                    flagsFound = true;
                    builder3.append((flag2.getSet() ? "+" : "-") + flag2.flagDefinition.getName()).append(" ");
                }
            }

            StringBuilder builder4 = new StringBuilder();
            flags = plugin.getFlagManager().getFlags(player.getWorld().getName());
            for (Flag flag3 : flags) {
                flagsFound = true;
                builder4.append((flag3.getSet() ? "+" : "-") + flag3.flagDefinition.getName()).append(" ");
            }

            StringBuilder builder5 = new StringBuilder();
            flags = plugin.getFlagManager().getFlags("everywhere");
            for (Flag flag4 : flags) {
                flagsFound = true;
                builder5.append((flag4.getSet() ? "+" : "-") + flag4.flagDefinition.getName()).append(" ");
            }

            if (builder1.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsClaim, builder1.toString());
            if (builder2.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsParent, builder2.toString());
            if (builder3.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsDefault, builder3.toString());
            if (builder4.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsWorld, builder4.toString());
            if (builder5.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsServer, builder5.toString());

            if (!flagsFound) {
                GPFlags.sendMessage(player, TextMode.Info, Messages.NoFlagsHere);
            }

            return true;
        }

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        if (claim == null) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.StandInAClaim);
            return true;
        }

        Long claimID = claim.getID();
        if (claimID == null || claimID == -1) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.UpdateGPForSubdivisionFlags);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("SetClaimFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            if (claim.allowEdit(player) != null) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NotYourClaim);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            // stop owner/ownermember fly flags from joining
            Collection<Flag> flags;
            flags = plugin.getFlagManager().getFlags(claim.getID().toString());
            for (Flag flag : flags) {
                if (args[0].equalsIgnoreCase("OwnerFly")) {
                    if (flag.flagDefinition.getName().equalsIgnoreCase("OwnerMemberFly")) {
                        GPFlags.sendMessage(player, TextMode.Warn, Messages.NoOwnerFlag);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("OwnerMemberFly")) {
                    if (flag.flagDefinition.getName().equalsIgnoreCase("OwnerFly")) {
                        GPFlags.sendMessage(player, TextMode.Warn, Messages.NoOwnerFlag);
                        return true;
                    }
                }
            }

            // SET BIOME
            if (flagName.equalsIgnoreCase("ChangeBiome")) {
                if (args.length < 2) return false;
                FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) plugin.getFlagManager().getFlagDefinitionByName("changebiome"));
                String biome = params[0].toUpperCase().replace(" ", "_");
                if (!flagD.changeBiome(sender, claim, biome)) return true;
            }

            // Permissions for mob type
            if (flagName.equalsIgnoreCase("NoMobSpawnsType")) {
                if (!player.hasPermission("gpflags.nomobspawnstype.*") && !player.hasPermission("gpflags.admin.*")) {
                    if (params == null) return false;
                    for (String type : params[0].split(";")) {
                        if (!player.hasPermission("gpflags.nomobspawnstype." + type)) {
                            GPFlags.sendMessage(player, TextMode.Err, Messages.MobTypePerm, type);
                            return true;
                        }
                    }
                }
            }

            SetFlagResult result = plugin.getFlagManager().setFlag(claimID.toString(), def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            if (result.success) plugin.getFlagManager().save();

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetClaimFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];

            FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (claim.allowEdit(player) != null) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NotYourClaim);
                return true;
            }

            // TODO RESET BIOME
            if (flagName.equalsIgnoreCase("ChangeBiome")) {
                FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) plugin.getFlagManager().getFlagDefinitionByName("changebiome"));
                flagD.resetBiome(claim.getID());
            }

            SetFlagResult result = plugin.getFlagManager().unSetFlag(claimID.toString(), def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            if (result.success) plugin.getFlagManager().save();

            return true;
        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        if (args.length == 0) {
            return ImmutableList.of();
        }

        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }

        String arg = builder.toString().trim();
        ArrayList<String> matches = new ArrayList<String>();
        String cmd = command.getName();
        for (String name : plugin.getFlagManager().getFlagDefinitionNames()) {
            if (StringUtil.startsWithIgnoreCase(name, arg)) {
                if (cmd.equalsIgnoreCase("setclaimflag") || cmd.equalsIgnoreCase("setdefaultclaimflag")
                        || cmd.equalsIgnoreCase("unsetclaimflag") || cmd.equalsIgnoreCase("unsetdefaultclaimflag")) {
                    if (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName(name).getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                        if (sender.hasPermission("gpflags." + name))
                            matches.add(name);
                    }
                }
                if (cmd.equalsIgnoreCase("setworldflag") || cmd.equalsIgnoreCase("unsetworldflag")) {
                    if (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName(name).getFlagType().contains(FlagDefinition.FlagType.WORLD)) {
                        if (sender.hasPermission("gpflags." + name))
                            matches.add(name);
                    }
                }
                if (cmd.equalsIgnoreCase("setserverflag") || cmd.equalsIgnoreCase("unsetserverflag")) {
                    if (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName(name).getFlagType().contains(FlagDefinition.FlagType.SERVER)) {
                        if (sender.hasPermission("gpflags." + name))
                            matches.add(name);
                    }
                }

            }
        }

        if (sender instanceof Player) {
            WorldSettings settings = GPFlags.getInstance().getWorldSettingsManager().get(((Player) sender).getWorld());


            // TabCompleter for Biomes in ChangeBiome flag
            if (args[0].equalsIgnoreCase("ChangeBiome")) {
                if (args.length != 2) return null;
                if (!(command.getName().equalsIgnoreCase("setclaimflag"))) return null;
                ArrayList<String> biomes = new ArrayList<>();
                for (Biome biome : Biome.values()) {
                    if (StringUtil.startsWithIgnoreCase(biome.toString(), args[1])) {
                        if (!(settings.biomeBlackList.contains(biome.toString()))) {
                            biomes.add(biome.toString());
                        } else if (sender.hasPermission("gpflags.bypass")) {
                            biomes.add(biome.toString());
                        }
                    }
                }
                biomes.sort(String.CASE_INSENSITIVE_ORDER);
                return biomes;
            }
        }
        if (args[0].equalsIgnoreCase("noOpenDoors")) {
            if (args.length != 2) return null;
            List<String> doortype = Arrays.asList("doors", "trapdoors", "gates");
            ArrayList<String> types = new ArrayList<>();
            for (String type : doortype) {
                if (StringUtil.startsWithIgnoreCase(type, args[1])) {
                    types.add(type);
                }
            }
            types.sort(String.CASE_INSENSITIVE_ORDER);
            return types;
        }
        matches.sort(String.CASE_INSENSITIVE_ORDER);
        return matches;
    }
}
