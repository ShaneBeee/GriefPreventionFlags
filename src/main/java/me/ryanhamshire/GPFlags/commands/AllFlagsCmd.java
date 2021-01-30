package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.command.CommandSender;

public class AllFlagsCmd extends BaseCmd {

    AllFlagsCmd(GPFlags plugin) {
        super(plugin);
        command = "AllFlags";
        usage = "";
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        for (FlagDefinition flag : PLUGIN.getFlagManager().getFlagDefinitions()) {
            sender.sendMessage(Util.getColString(flag.getName() + " &7" + flag.getFlagType()));
        }
        return true;
    }

}
