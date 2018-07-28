package me.ryanhamshire.GPFlags;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

public class FlagDef_InfiniteArrows extends FlagDefinition
{
    @EventHandler(priority = EventPriority.LOW)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if(event.getEntityType() != EntityType.ARROW && event.getEntityType() != EntityType.SNOWBALL) return;
        
        Projectile arrow = event.getEntity();
        
        ProjectileSource source = arrow.getShooter();
        if(source == null || !(source instanceof Player)) return;
        
        Player player = (Player)source;
        if(player.getGameMode() == GameMode.CREATIVE) return;
        
        Flag flag = this.GetFlagInstanceAtLocation(arrow.getLocation(), player);
        if(flag == null) return;
        
        PlayerInventory inventory = player.getInventory();
        ItemMeta meta = inventory.getItemInMainHand().getItemMeta();
        if(meta != null && meta.hasEnchant(Enchantment.ARROW_INFINITE)) return;
        
        if(event.getEntityType() == EntityType.ARROW) 
        {
         arrow.remove();
         inventory.addItem(new ItemStack(Material.ARROW));
         return;
        }
        
        if(event.getEntityType() == EntityType.SNOWBALL) 
        {
         arrow.remove();
         inventory.addItem(new ItemStack(Material.SNOWBALL));
         return;
        }
    }
    
    public FlagDef_InfiniteArrows(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "InfiniteArrows";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableInfiniteArrows);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableInfiniteArrows);
    }
}
