package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDef_ChangeBiome;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.flags.FlagDefinition.FlagType;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

class SetClaimFlagCmd extends BaseCmd {

    private final Collection<String> flagDefinitionNames;



    SetClaimFlagCmd(GPFlags plugin) {
        super(plugin);
        command = "SetClaimFlag";
        usage = "<flag> [<parameters>]";
        requirePlayer = true;
        flagDefinitionNames = plugin.getFlagManager().getFlagDefinitionNames();
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String flagName = args[0];
            Player player = ((Player) sender);

            FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
            if (def == null) {
                Util.sendMessage(player, TextMode.Warn, getFlagDefsMessage(player));
                return true;
            }

            if (!playerHasPermissionForFlag(def, player)) {
                Util.sendMessage(player, TextMode.Err, Messages.NoFlagPermission, flagName);
                return true;
            }

            if (!def.getFlagType().contains(FlagType.CLAIM)) {
                Util.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
            if (claim.allowEdit(player) != null) {
                Util.sendMessage(player, TextMode.Err, Messages.NotYourClaim);
                return true;
            }

            String[] params = new String[args.length - 1];
            System.arraycopy(args, 1, params, 0, args.length - 1);

            // stop owner/ownermember fly flags from joining
            Collection<Flag> flags;
            flags = PLUGIN.getFlagManager().getFlags(claim.getID().toString());
            for (Flag flag : flags) {
                if (flagName.equalsIgnoreCase("OwnerFly")) {
                    if (flag.getFlagDefinition().getName().equalsIgnoreCase("OwnerMemberFly")) {
                        Util.sendMessage(player, TextMode.Warn, Messages.NoOwnerFlag);
                        return true;
                    }
                }
                if (flagName.equalsIgnoreCase("OwnerMemberFly")) {
                    if (flag.getFlagDefinition().getName().equalsIgnoreCase("OwnerFly")) {
                        Util.sendMessage(player, TextMode.Warn, Messages.NoOwnerFlag);
                        return true;
                    }
                }
            }

            // SET BIOME
            if (flagName.equalsIgnoreCase("ChangeBiome")) {
                if (args.length < 2) return false;
                FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) PLUGIN.getFlagManager().getFlagDefinitionByName("changebiome"));
                String biome = params[0].toUpperCase().replace(" ", "_");
                if (!flagD.changeBiome(sender, claim, biome)) return true;
            }

            // Permissions for mob type
            if (flagName.equalsIgnoreCase("NoMobSpawnsType")) {
                if (!player.hasPermission("gpflags.flag.nomobspawnstype.*") && !player.hasPermission("gpflags.nomobspawnstype.*") && !player.hasPermission("gpflags.admin.*")) {
                    if (params.length == 0) return false;
                    for (String type : params[0].split(";")) {
                        if (!playerHasPermissionForEntity(player, type)) {
                            Util.sendMessage(player, TextMode.Err, Messages.MobTypePerm, type);
                            return true;
                        }
                    }
                }
            }

            Long claimID = claim.getID();
            if (claimID == null || claimID == -1) {
                Util.sendMessage(player, TextMode.Err, Messages.UpdateGPForSubdivisionFlags);
                return true;
            }

            SetFlagResult result = PLUGIN.getFlagManager().setFlag(claimID.toString(), def, true, params);
            ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
            Util.sendMessage(player, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
            if (result.isSuccess()) FLAG_MANAGER.save();

            if (args[0].equalsIgnoreCase("NoEnter") && args.length >= 2) {
                World world = player.getWorld();
                for (Player p : world.getPlayers()) {
                    if (claim.contains(p.getLocation(), true, false)) {
                        if (claim.getPermission(p.getName()) == null && !claim.getOwnerName().equals(p.getName())) {
                            GriefPrevention.instance.ejectPlayer(p);
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("NoEnterPlayer") && args.length >= 2) {
                for (int i = 1; i < args.length; i++) {
                    Player target = Bukkit.getPlayer(args[i]);
                    if (target != null && target.getName().equals(args[i])) {
                        if (claim.contains(target.getLocation(), true, false)) {
                            if (claim.getPermission(args[i]) == null) {
                                GriefPrevention.instance.ejectPlayer(target);
                            }
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("OwnerFly")) {
                player.setAllowFlight(true);
            }
            if (args[0].equalsIgnoreCase("OwnerMemberFly")) {
                player.setAllowFlight(true);
                World world = player.getWorld();
                for (Player p : world.getPlayers()) {
                    if (claim.contains(p.getLocation(), true, false)) {
                        if (claim.getPermission(p.getUniqueId().toString()) == ClaimPermission.Access) {
                            p.setAllowFlight(true);
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return flagTab(sender, args[0]);
        } else if (args.length == 2) {
            return paramTab(sender, args);
        }
        return Collections.emptyList();
    }

}
