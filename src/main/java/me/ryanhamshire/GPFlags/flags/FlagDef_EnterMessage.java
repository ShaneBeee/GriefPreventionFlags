package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_EnterMessage extends PlayerMovementFlagDefinition {

    private final String prefix;

    public FlagDef_EnterMessage(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
        this.prefix = plugin.getFlagsDataStore().getMessage(Messages.EnterExitPrefix);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return true;
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if (flag == null) return true;

        // get specific EnterMessage flag of destination claim and ExitMessage flag of origin claim
        Flag flagTo = plugin.getFlagManager().getFlag(claimTo, this);
        Flag flagFromExit = plugin.getFlagManager().getFlag(claimFrom, plugin.getFlagManager().getFlagDefinitionByName("ExitMessage"));

        // Don't repeat the enter message of a claim in certain cases
        if (claimFrom != null && claimTo != null) {
            // moving to sub-claim, and the sub claim does not have its own enter message
            if (claimTo.parent == claimFrom && (flagTo == null || !flagTo.getSet())) {
                return true;
            }
            // moving to parent claim, and the sub claim does not have its own exit message
            if (claimFrom.parent == claimTo && (flagFromExit == null || !flagFromExit.getSet())) {
                return true;
            }
        }

        String message = flag.parameters;
        if (claimTo != null) {
            message = message.replace("%owner%", claimTo.getOwnerName()).replace("%name%", player.getName());
        }

        Util.sendMessage(player, TextMode.Info, prefix + message);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        String message = flag.parameters;
        message = message.replace("%owner%", claim.getOwnerName()).replace("%name%", player.getName());
        Util.sendMessage(player, TextMode.Info, prefix + message);
    }

    @Override
    public String getName() {
        return "EnterMessage";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.MessageRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddedEnterMessage, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedEnterMessage);
    }

}
