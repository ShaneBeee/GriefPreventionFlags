package me.ryanhamshire.GPFlags;

//import org.bukkit.entity.Entity;
//import org.bukkit.entity.EntityType;
//import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
//import org.bukkit.event.player.PlayerPickupItemEvent;

public class FlagDef_NoItemPickup extends FlagDefinition
{
    @EventHandler(priority = EventPriority.LOWEST)
    //где-то тут я лазил. PlayerPickupItemEvent -> EntityPickupItemEvent
    public void onPlayerPickupItem(EntityPickupItemEvent event)
    {
    	if (event.getEntity() instanceof Player) 
    	{
    	 Player player = (Player)event.getEntity();	 
         Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
         if(flag == null) return;
         ((Cancellable) event).setCancelled(true);
        }
        
    }
    
    public FlagDef_NoItemPickup(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "NoItemPickup";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNoItemPickup);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNoItemPickup);
    }
}
