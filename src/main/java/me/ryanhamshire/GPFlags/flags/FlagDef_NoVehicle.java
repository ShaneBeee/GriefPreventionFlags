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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoVehicle extends FlagDefinition {

    public FlagDef_NoVehicle(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler
    private void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        for (Entity entity : vehicle.getPassengers()) {
            if (entity instanceof Player) {
                Player player = ((Player) entity);
                handleVehicleMovement(player, vehicle, event.getFrom(), event.getTo());
            }
        }
    }

    @EventHandler
    private void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof Vehicle) {
            handleVehicleMovement(player, ((Vehicle) vehicle), event.getFrom(), event.getTo());
        }
    }

    private void handleVehicleMovement(Player player, Vehicle vehicle, Location locFrom, Location locTo) {
        Flag flag = this.getFlagInstanceAtLocation(locTo, player);
        if (flag != null) {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(locTo, false, null);
            if (!claim.hasExplicitPermission(player, ClaimPermission.Inventory)) {
                vehicle.eject();
                ItemStack itemStack = Util.getItemFromVehicle(vehicle);
                if (itemStack != null) {
                    vehicle.getWorld().dropItem(locFrom, itemStack);
                }
                vehicle.remove();
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoVehicleAllowed);
            }
        }
    }

    @EventHandler
    private void onPlaceVehicle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        EquipmentSlot hand = event.getHand();
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        if ((Util.isAVehicle(inventory.getItemInMainHand()) && hand == EquipmentSlot.HAND) ||
                (Util.isAVehicle(inventory.getItemInOffHand()) && hand == EquipmentSlot.OFF_HAND)) {
            Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
            if (flag == null) return;
            event.setCancelled(true);
            GPFlags.sendMessage(player, TextMode.Err, Messages.NoPlaceVehicle);
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
                if (claim != null && !claim.hasExplicitPermission(player, ClaimPermission.Inventory)) {
                    event.setCancelled(true);
                    GPFlags.sendMessage(player, TextMode.Err, Messages.NoEnterVehicle);
                }
            }
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
