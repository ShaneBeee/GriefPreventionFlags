package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetWorldFlagCmd extends BaseCmd {

    SetWorldFlagCmd(GPFlags plugin) {
        super(plugin);
        command = "SetWorldFlag";
        usage = "<world> <flag> [<parameters>]";
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return false;

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            Util.sendMessage(sender, TextMode.Err, Messages.WorldNotFound, args[0]);
            return true;
        }

        String flagName = args[1];
        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(sender, TextMode.Err, getFlagDefsMessage(sender));
            return true;
        }

        if (!playerHasPermissionForFlag(def, sender)) {
            Util.sendMessage(sender, TextMode.Err, Messages.NoFlagPermission);
            return true;
        }

        if (!def.getFlagType().contains(FlagDefinition.FlagType.WORLD)) {
            Util.sendMessage(sender, TextMode.Err, Messages.NoFlagInWorld);
            return true;
        }

        String[] params = new String[args.length - 2];
        System.arraycopy(args, 2, params, 0, args.length - 2);

        SetFlagResult result = PLUGIN.getFlagManager().setFlag(world.getName(), def, true, params);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        if (result.isSuccess()) {
            Util.sendMessage(sender, color, Messages.WorldFlagSet);
            PLUGIN.getFlagManager().save();
        } else {
            Util.sendMessage(sender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        }

        return true;
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> worlds = new ArrayList<>();
            Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
            return StringUtil.copyPartialMatches(args[0], worlds, new ArrayList<>());
        } else if (args.length == 2) {
            return flagTab(sender, args[1]);
        } else if (args.length == 3) {
            return paramTab(sender, args);
        }
        return Collections.emptyList();
    }

}
