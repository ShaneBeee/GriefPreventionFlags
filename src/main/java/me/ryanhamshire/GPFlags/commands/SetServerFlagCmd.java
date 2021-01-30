package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class SetServerFlagCmd extends BaseCmd {
    
    SetServerFlagCmd(GPFlags plugin) {
        super(plugin);
        command = "SetServerFlag";
        usage = "<flag> [<parameters>]";
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) return false;

        String flagName = args[0];
        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(sender, TextMode.Err, getFlagDefsMessage(sender));
            return true;
        }

        if (!playerHasPermissionForFlag(def, sender)) {
            Util.sendMessage(sender, TextMode.Err, Messages.NoFlagPermission);
            return true;
        }

        if (!def.getFlagType().contains(FlagDefinition.FlagType.SERVER)) {
            Util.sendMessage(sender, TextMode.Err, Messages.NoFlagInServer);
            return true;
        }

        String[] params = new String[args.length - 1];
        System.arraycopy(args, 1, params, 0, args.length - 1);

        SetFlagResult result = PLUGIN.getFlagManager().setFlag("everywhere", def, true, params);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        if (result.isSuccess()) {
            Util.sendMessage(sender, color, Messages.ServerFlagSet);
            PLUGIN.getFlagManager().save();
        } else {
            Util.sendMessage(sender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        }

        return true;
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
