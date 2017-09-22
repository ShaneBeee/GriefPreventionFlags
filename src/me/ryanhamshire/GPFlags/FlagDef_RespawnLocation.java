package me.ryanhamshire.GPFlags;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class FlagDef_RespawnLocation extends FlagDefinition
{
    ConcurrentHashMap<UUID, Location> respawnMap = new ConcurrentHashMap<UUID, Location>();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Location location = player.getLocation();
        
        Flag flag = this.GetFlagInstanceAtLocation(location, player);
        if(flag == null) return;
        
        String [] params = flag.getParametersArray();
        World respawnWorld = Bukkit.getServer().getWorld(params[0]); 
        Location respawnLocation = new Location
        (
            respawnWorld,
            Double.valueOf(params[1]),
            Double.valueOf(params[2]),
            Double.valueOf(params[3]),
            params.length < 5 ? 0 : Float.valueOf(params[4]),
            params.length < 6 ? 0 : Float.valueOf(params[5])
        );
        
        this.respawnMap.put(player.getUniqueId(), respawnLocation);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        Location respawnLocation = this.respawnMap.get(player.getUniqueId());
        if(respawnLocation != null)
        {
            event.setRespawnLocation(respawnLocation);
            this.respawnMap.remove(player.getUniqueId());
        }
    }
    
    public FlagDef_RespawnLocation(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        String [] params = parameters.split(" ");
        
        if(params.length < 4)
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.LocationRequired));
        }
        
        World world = Bukkit.getWorld(params[0]);
        if(world == null)
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.WorldNotFound));
        }
        
        try
        {
            Double.valueOf(params[1]);
            Double.valueOf(params[2]);
            Double.valueOf(params[3]);
            if(params.length > 4 ) Float.valueOf(params[4]);
            if(params.length > 5 ) Float.valueOf(params[5]);
        }
        catch(NumberFormatException e)
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.LocationRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }
    
    @Override
    String getName()
    {
        return "RespawnLocation";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.SetRespawnLocation);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.UnSetRespawnLocation);
    }
}
