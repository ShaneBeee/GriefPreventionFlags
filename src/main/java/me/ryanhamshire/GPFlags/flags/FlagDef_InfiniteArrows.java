package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;

public class FlagDef_InfiniteArrows extends FlagDefinition {

    @EventHandler(priority = EventPriority.LOW)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.ARROW && event.getEntityType() != EntityType.SNOWBALL) return;

        ProjectileSource source = event.getEntity().getShooter();
        if (!(source instanceof Player)) return;

        Player player = (Player) source;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        Flag flag = this.GetFlagInstanceAtLocation(event.getEntity().getLocation(), player);
        if (flag == null) return;

        PlayerInventory inventory = player.getInventory();

        if (event.getEntityType() == EntityType.SNOWBALL) {
            inventory.addItem(new ItemStack(Material.SNOWBALL));
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();
        if (arrow.getPickupStatus() != AbstractArrow.PickupStatus.ALLOWED) return;

        arrow.remove();
        inventory.addItem(new ItemStack(Material.ARROW));

    }

    public FlagDef_InfiniteArrows(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "InfiniteArrows";
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableInfiniteArrows);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableInfiniteArrows);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
