package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoVehicle extends FlagDefinition {

    public FlagDef_NoVehicle(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler
    private void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        List<Entity> passengers = vehicle.getPassengers();
        if (passengers.size() == 0) return;
        Entity passenger = passengers.get(0);
        if (!(passenger instanceof Player)) return;
        Player player = (Player) passenger;
        handleVehicleMovement(player, vehicle, event.getFrom(), event.getTo(), false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof Vehicle) {
            handleVehicleMovement(player, (Vehicle) vehicle, event.getFrom(), event.getTo(), true);
        }
    }

    private void handleVehicleMovement(Player player, Vehicle vehicle, Location locFrom, Location locTo, boolean isTeleportEvent) {
        Flag flag = this.getFlagInstanceAtLocation(locTo, player);
        if (flag != null) {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(locTo, false, null);
            if (claim.getOwnerName().equals(player.getName())) return;
            if (claim.hasExplicitPermission(player, ClaimPermission.Inventory)) return;
            if (isTeleportEvent) {
                player.leaveVehicle();
                Util.sendMessage(player, TextMode.Err, Messages.NoVehicleAllowed);
                return;
            }
            vehicle.eject();
            ItemStack itemStack = Util.getItemFromVehicle(vehicle);
            if (itemStack != null) {
                if (vehicle.isValid()) {
                    vehicle.getWorld().dropItem(locFrom, itemStack);
                    vehicle.remove();
                }
            }
            Util.sendMessage(player, TextMode.Err, Messages.NoVehicleAllowed);
        }
    }

    @EventHandler
    private void onMount(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        Vehicle vehicle = event.getVehicle();
        if (entity instanceof Player && (vehicle instanceof Boat || vehicle instanceof Minecart)) {
            Player player = ((Player) entity);

            Flag flag = this.getFlagInstanceAtLocation(vehicle.getLocation(), player);
            if (flag != null) {
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(vehicle.getLocation(), false, null);
                if (claim != null && !claim.hasExplicitPermission(player, ClaimPermission.Inventory) && !claim.getOwnerName().equals(player.getName())) {
                    event.setCancelled(true);
                    Util.sendMessage(player, TextMode.Err, Messages.NoEnterVehicle);
                }
            }
        }
    }

    @EventHandler
    private void onCollision(VehicleEntityCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        Flag flag = this.getFlagInstanceAtLocation(vehicle.getLocation(), null);
        if (flag != null) {
            Entity entity = event.getEntity();
            if (entity instanceof Player) {
                Player player = (Player) entity;
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(vehicle.getLocation(), false, null);
                if (claim == null) return;
                if (claim.getOwnerName().equals(player.getName())) return;
                if (claim.hasExplicitPermission(player, ClaimPermission.Inventory)) return;
            }
            event.setCollisionCancelled(true);
            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "NoVehicle";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoVehicle);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoVehicle);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
