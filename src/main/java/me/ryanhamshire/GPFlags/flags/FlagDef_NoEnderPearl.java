package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.FlagsDataStore;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoEnderPearl extends FlagDefinition {

    public FlagDef_NoEnderPearl(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != TeleportCause.ENDER_PEARL) return;

        Player player = event.getPlayer();

        Flag flag = this.GetFlagInstanceAtLocation(event.getFrom(), event.getPlayer());
        if (flag != null) {
            event.setCancelled(true);
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getFrom(), true, null);
            if (claim != null) {
                String owner = claim.getOwnerName();

                String msg = new FlagsDataStore().getMessage(Messages.NoEnderPearlInClaim);
                Util.sendMessage(player, TextMode.Warn, msg.replace("{o}", owner).replace("{p}", player.getName()));
                return;
            }
            String msg = new FlagsDataStore().getMessage(Messages.NoEnderPearlInWorld);
            Util.sendMessage(player, TextMode.Warn, msg.replace("{p}", player.getName()));
            return;
        }

        flag = this.GetFlagInstanceAtLocation(event.getTo(), event.getPlayer());
        if (flag != null) {
            event.setCancelled(true);
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getTo(), true, null);
            if (claim != null) {
                String owner = claim.getOwnerName();

                String msg = new FlagsDataStore().getMessage(Messages.NoEnderPearlToClaim);
                Util.sendMessage(player, TextMode.Warn, msg.replace("{o}", owner).replace("{p}", player.getName()));
            }
        }

    }

    @Override
    public String getName() {
        return "NoEnderPearl";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoEnderPearl);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoEnderPearl);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
