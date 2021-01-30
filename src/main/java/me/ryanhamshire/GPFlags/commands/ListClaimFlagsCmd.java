package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ListClaimFlagsCmd extends BaseCmd {

    ListClaimFlagsCmd(GPFlags plugin) {
        super(plugin);
        command = "ListClaimFlags";
        usage = "";
        requirePlayer = true;
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        Player player = ((Player) sender);
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        Collection<Flag> flags;
        boolean flagsFound = false;
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        StringBuilder builder3 = new StringBuilder();
        if (claim != null) {
            flags = PLUGIN.getFlagManager().getFlags(claim.getID().toString());
            for (Flag flag : flags) {
                flagsFound = true;
                builder1.append((flag.getSet() ? "+" : "-") + flag.getFlagDefinition().getName()).append(" ");
            }

            if (claim.parent != null) {
                flags = PLUGIN.getFlagManager().getFlags(claim.parent.getID().toString());
                for (Flag flag : flags) {
                    flagsFound = true;
                    builder2.append((flag.getSet() ? "+" : "-") + flag.getFlagDefinition().getName()).append(" ");
                }
            }

            flags = PLUGIN.getFlagManager().getFlags(FlagManager.DEFAULT_FLAG_ID);
            for (Flag flag2 : flags) {
                flagsFound = true;
                builder3.append((flag2.getSet() ? "+" : "-") + flag2.getFlagDefinition().getName()).append(" ");
            }
        }

        StringBuilder builder4 = new StringBuilder();
        flags = PLUGIN.getFlagManager().getFlags(player.getWorld().getName());
        for (Flag flag3 : flags) {
            flagsFound = true;
            builder4.append((flag3.getSet() ? "+" : "-") + flag3.getFlagDefinition().getName()).append(" ");
        }

        StringBuilder builder5 = new StringBuilder();
        flags = PLUGIN.getFlagManager().getFlags("everywhere");
        for (Flag flag4 : flags) {
            flagsFound = true;
            builder5.append((flag4.getSet() ? "+" : "-") + flag4.getFlagDefinition().getName()).append(" ");
        }

        if (builder1.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsClaim, builder1.toString());
        if (builder2.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsParent, builder2.toString());
        if (builder3.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsDefault, builder3.toString());
        if (builder4.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsWorld, builder4.toString());
        if (builder5.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsServer, builder5.toString());

        if (!flagsFound) {
            Util.sendMessage(player, TextMode.Info, Messages.NoFlagsHere);
        }

        return true;
    }

}
