package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class UnsetClaimFlagPlayerCmd extends BaseCmd {

    UnsetClaimFlagPlayerCmd(GPFlags plugin) {
        super(plugin);
        command = "UnsetClaimFlagPlayer";
        usage = "<player> <flag>";
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return false;
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Util.sendMessage(sender, "&c" + args[0] + " &7is not online");
            return false;
        }
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
        if (claim == null || claim.allowEdit(player) != null) {
            Util.sendMessage(sender, "&cThis player is not standing in a claim they own");
            return false;
        }

        String flagName = args[1];
        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(sender, "&c%s&7 is not a valid flag", flagName);
            return false;
        }
        if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
            Util.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
            return true;
        }

        SetFlagResult result = PLUGIN.getFlagManager().unSetFlag(claim, def);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        Util.sendMessage(sender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        String message;
        if (result.isSuccess()) {
            PLUGIN.getFlagManager().save();
            message = "&7Flag &b%s &7successfully unset in &b%s&7's claim.";

        } else {
            message = "&cFlag &b%s &cfailed to unset in &b%s&c's claim.";
        }
        Util.sendMessage(sender, message, def.getName(), player.getName());

        return true;
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return null; // returns player list
        } else if (args.length == 2) {
            return flagTab(sender, args[1]);
        }
        return Collections.emptyList();
    }

}
