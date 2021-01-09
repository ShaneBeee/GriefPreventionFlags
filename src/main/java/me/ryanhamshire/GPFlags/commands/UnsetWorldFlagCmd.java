package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class UnsetWorldFlagCmd extends BaseCmd {

    UnsetWorldFlagCmd(GPFlags plugin) {
        super(plugin);
        command = "UnsetWorldFlag";
        usage = "<flag>";
        requirePlayer = true;
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) return false;

        Player player = ((Player) sender);
        String flagName = args[0];
        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
            return true;
        }

        if (!playerHasPermissionForFlag(def, player)) {
            Util.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
            return true;
        }

        SetFlagResult result = PLUGIN.getFlagManager().unSetFlag(player.getWorld().getName(), def);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        if (result.isSuccess()) {
            Util.sendMessage(player, color, Messages.WorldFlagUnSet);
            PLUGIN.getFlagManager().save();
        } else {
            Util.sendMessage(player, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        }

        return true;
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return flagTab(sender, args[0]);
        }
        return Collections.emptyList();
    }

}
