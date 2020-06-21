package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.message.Message;
import me.ryanhamshire.GPFlags.message.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_OwnerMemberFly extends PlayerMovementFlagDefinition implements Listener {

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claim) {
        if (lastLocation == null) return true;
        Flag flag = GPFlags.getInstance().getFlagManager().getFlag(claim, this);
        Flag ownerFly = GPFlags.getInstance().getFlagManager().getFlag(claim, "OwnerFly");

        if (flag == null && ownerFly == null) {
            if (claim != null) {
                Flag noFlight = GPFlags.getInstance().getFlagManager().getFlag(claim, GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("NoFlight"));
                if (noFlight != null && !noFlight.getSet()) {
                    return true;
                }
            }
            GameMode mode = player.getGameMode();
            if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR && player.isFlying() &&
                    !player.hasPermission("gpflags.bypass.fly") && !player.hasPermission("gpflags.bypass")) {
                Block block = player.getLocation().getBlock();
                while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.setAllowFlight(false);
                if (player.getLocation().getY() - block.getY() >= 4) {
                    GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                }
                GPFlags.sendMessage(player, TextMode.WARNING, Messages.FLIGHT_EXIT_DISABLED.getText());
                return true;
            }
            if (player.getAllowFlight() && mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR &&
                    !player.hasPermission("gpflags.bypass.fly") && !player.hasPermission("gpflags.bypass")) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.WARNING, Messages.FLIGHT_EXIT_DISABLED.getText());
            }
            return true;
        }
        if (flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;
        if (flag == null) return true;
        if (claim == null) return true;

        if (claim.allowAccess(player) == null) {
            player.setAllowFlight(true);
            GPFlags.sendMessage(player, TextMode.SUCCESS, Messages.FLIGHT_ENTER_ENABLED.getText());
            return true;
        } else {
            GameMode mode = player.getGameMode();
            if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR && player.isFlying() &&
                    !player.hasPermission("gpflags.bypass.fly") && !player.hasPermission("gpflags.bypass")) {
                GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.WARNING, Messages.FLIGHT_EXIT_DISABLED.getText());
            }
            if (player.getAllowFlight() && mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR &&
                    !player.hasPermission("gpflags.bypass.fly") && !player.hasPermission("gpflags.bypass")) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.WARNING, Messages.FLIGHT_EXIT_DISABLED.getText());
            }
        }
        return true;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        Material below = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);

        if (flag != null && claim.allowAccess(player) == null) {
            player.setAllowFlight(true);
            if (below == Material.AIR) {
                player.setFlying(true);
            }
        }
    }

    public FlagDef_OwnerMemberFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }


    @Override
    public String getName() {
        return "OwnerMemberFly";
    }

    @Override
    public Message getSetMessage() {
        return Messages.FLIGHT_OWNER_MEMBER_ENABLED;
    }

    @Override
    public Message getUnSetMessage() {
        return Messages.FLIGHT_OWNER_MEMBER_DISABLED;
    }

}
