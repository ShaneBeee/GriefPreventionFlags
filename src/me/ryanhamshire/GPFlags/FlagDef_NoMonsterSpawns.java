package me.ryanhamshire.GPFlags;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FlagDef_NoMonsterSpawns extends FlagDefinition
{
    private final String ALLOW_TARGET_TAG = "GPF_AllowTarget";
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        LivingEntity entity = event.getEntity();
        if(!this.isMonster(entity)) return;
        
        SpawnReason reason = event.getSpawnReason();
        if(reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG)
        {
            entity.setMetadata(this.ALLOW_TARGET_TAG, new FixedMetadataValue(GPFlags.instance, new Boolean(true)));
            return;
        }
        
        Flag flag = this.GetFlagInstanceAtLocation(event.getLocation(), null);
        if(flag == null) return;
        
        event.setCancelled(true);
    }
    
    private boolean isMonster(Entity entity)
    {
        if(entity instanceof Monster) return true;
        
        EntityType type = entity.getType();
        if(type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SHULKER || type == EntityType.PHANTOM) return true;
        
        if(type == EntityType.RABBIT)
        {
            Rabbit rabbit = (Rabbit)entity;
            if(rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY) return true;
        }
        
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event)
    {
        Entity target = event.getTarget();
        if(target == null) return;
        
        Entity entity = event.getEntity();
        if(!this.isMonster(entity)) return;
        if(entity.hasMetadata(this.ALLOW_TARGET_TAG)) return;
        
        Flag flag = this.GetFlagInstanceAtLocation(target.getLocation(), null);
        if(flag == null) return;
        
        event.setCancelled(true);
        entity.remove();
    }
    
    public FlagDef_NoMonsterSpawns(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "NoMonsterSpawns";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.DisableMonsterSpawns);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.EnableMonsterSpawns);
    }
}
