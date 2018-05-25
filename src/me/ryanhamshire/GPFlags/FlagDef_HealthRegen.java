package me.ryanhamshire.GPFlags;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class FlagDef_HealthRegen extends TimedPlayerFlagDefinition
{
    @Override
    long getPlayerCheckFrequency_Ticks()
    {
        return 100L;
    }

    @Override
    void processPlayer(Player player)
    {
        if((player.getHealth() >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) || player.isDead() == true) return;
        
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        int healAmount = 2;
        if(flag.parameters != null && !flag.parameters.isEmpty())
        {
            try
            {
                healAmount = Integer.parseInt(flag.parameters);
            }
            catch(NumberFormatException e)
            {
                GPFlags.AddLogEntry("Problem with health regen amount @ " + player.getLocation().getBlock().getLocation().toString());
            }
        }
        
        int newHealth = healAmount + (int)player.getHealth();
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), newHealth));
    }
    
    /*@EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if(event.getCause() != DamageCause.STARVATION) return;
        if(event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player)event.getEntity();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        player.setHealth(player.getHealth() + 1);
        player.setSaturation(player.getSaturation() + 1);
    }*/
    
    public FlagDef_HealthRegen(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "HealthRegen";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableHealthRegen);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableHealthRegen);
    }
    
    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty()) return new SetFlagResult(false, new MessageSpecifier(Messages.HealthRegenGreaterThanZero));
        
        int amount;
        try
        {
            amount = Integer.parseInt(parameters);
            if(amount <= 0)
            {
                return new SetFlagResult(false, new MessageSpecifier(Messages.HealthRegenGreaterThanZero));
            }
        }
        catch(NumberFormatException e)
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.HealthRegenGreaterThanZero));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }
}
