package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("WeakerAccess")
public class Util {

    /** Check if server is running a minimum Minecraft version
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /** Check if server is running a minimum Minecraft version
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int maj = Integer.parseInt(version[0]);
        int min = Integer.parseInt(version[1]);
        int rev;
        try {
            rev = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj > major || min > minor || (min == minor && rev >= revision);
    }


    /** Disable the flight mode of a player whom cant fly
     * <p>This is mainly used when a player deletes their claim.</p>
     * @param player Player to disable flight for
     */
    public static void disableFlight(Player player) {
        if (player.isFlying() && !canFly(player)) {
            Location loc = player.getLocation();
            Block block = loc.getBlock();
            while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                block = block.getRelative(BlockFace.DOWN);
            }
            player.setAllowFlight(false);
            if (loc.getY() - block.getY() >= 4) {
                GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
            }
            GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
        if (player.getAllowFlight() && !canFly(player)) {
            player.setAllowFlight(false);
            GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
    }

    private static boolean canFly(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SPECTATOR || mode == GameMode.CREATIVE ||
                player.hasPermission("gpflags.bypass.fly") || player.hasPermission("gpflags.bypass");
    }

    /**
     * Get the ItemStack form of a vehicle
     * <p>Specifically a boat or minecart</p>
     *
     * @param vehicle Vehicle to get item from
     * @return ItemStack that matches vehicle
     */
    public static ItemStack getItemFromVehicle(Vehicle vehicle) {
        Material material = null;
        if (vehicle instanceof Boat) {
            switch (((Boat) vehicle).getWoodType()) {
                case BIRCH:
                    material = Material.BIRCH_BOAT;
                    break;
                case ACACIA:
                    material = Material.ACACIA_BOAT;
                    break;
                case JUNGLE:
                    material = Material.JUNGLE_BOAT;
                    break;
                case REDWOOD:
                    material = Material.SPRUCE_BOAT;
                    break;
                case DARK_OAK:
                    material = Material.DARK_OAK_BOAT;
                    break;
                default:
                    material = Material.OAK_BOAT;
            }
        } else if (vehicle instanceof RideableMinecart) {
            material = Material.MINECART;
        } else if (vehicle instanceof StorageMinecart) {
            material = Material.CHEST_MINECART;
        } else if (vehicle instanceof CommandMinecart) {
            material = Material.COMMAND_BLOCK_MINECART;
        } else if (vehicle instanceof ExplosiveMinecart) {
            material = Material.TNT_MINECART;
        } else if (vehicle instanceof HopperMinecart) {
            material = Material.HOPPER_MINECART;
        } else if (vehicle instanceof PoweredMinecart) {
            material = Material.FURNACE_MINECART;
        }
        if (material != null) {
            return new ItemStack(material);
        }
        return null;
    }

    /**
     * Check if an ItemStack is a vehicle
     * <p>Specifically a boat or minecart</p>
     *
     * @param itemStack ItemStack to check
     * @return True if item is a vehicle
     */
    public static boolean isAVehicle(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case MINECART:
            case CHEST_MINECART:
            case COMMAND_BLOCK_MINECART:
            case FURNACE_MINECART:
            case HOPPER_MINECART:
            case TNT_MINECART:
            case BIRCH_BOAT:
            case ACACIA_BOAT:
            case DARK_OAK_BOAT:
            case JUNGLE_BOAT:
            case OAK_BOAT:
            case SPRUCE_BOAT:
                return true;
        }
        return false;
    }

}
