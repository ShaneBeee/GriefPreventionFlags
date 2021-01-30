package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.command.CommandSender;

public class ReloadCmd extends BaseCmd {

    ReloadCmd(GPFlags plugin) {
        super(plugin);
        command = "Reload";
        usage = "";
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        PLUGIN.reloadConfig();
        Util.sendMessage(sender, TextMode.Success, Messages.ReloadComplete);
        return true;
    }

}
