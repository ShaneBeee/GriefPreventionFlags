package me.ryanhamshire.GPFlags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class FlagDef_NetherPortalConsoleCommand extends FlagDefinition
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        TeleportCause cause = event.getCause();
        
        if(cause == null || cause != TeleportCause.NETHER_PORTAL) return;
        
        Location from = event.getFrom();
        if(from.getBlock().getType() != Material.NETHER_PORTAL)
        {
            event.setCancelled(true);
            return;
        }
        
        Player player = event.getPlayer();
        
        Flag flag = this.GetFlagInstanceAtLocation(from, player);
        if(flag == null) return;
        
        event.setCancelled(true);
        String [] commandLines = flag.parameters.replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()).split(";");
        for(String commandLine : commandLines)
        {
            GPFlags.AddLogEntry("Nether portal command: " + commandLine);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandLine);
        }
    }
    
    public FlagDef_NetherPortalConsoleCommand(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.ConsoleCommandRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }
    
    @Override
    String getName()
    {
        return "NetherPortalConsoleCommand";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNetherPortalConsoleCommand);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNetherPortalConsoleCommand);
    }
}
