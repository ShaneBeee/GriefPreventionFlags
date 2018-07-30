package me.ryanhamshire.GPFlags;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlagDef_NoFlight extends TimedPlayerFlagDefinition
{
    @Override
    long getPlayerCheckFrequency_Ticks()
    {
        return 30L;
    }

    @Override
    void processPlayer(Player player)
    {
        if(!player.isFlying()) return;
        if(player.hasPermission("gpflags.bypass")) return;
        
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        GPFlags.sendMessage(player, TextMode.Err, Messages.CantFlyHere);
        player.setFlying(false);
        GameMode mode = player.getGameMode();
        if(mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR)
        {
        	Block block = player.getLocation().getBlock();
        	while(block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER)
        	{
        		block = block.getRelative(BlockFace.DOWN);
        	}
        	
        	player.teleport(block.getRelative(BlockFace.UP).getLocation());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event)
    {
        Player player = event.getPlayer();
    	if(player.isFlying()) return;
        if(player.hasPermission("gpflags.bypass")) return;
        
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        GPFlags.sendMessage(player, TextMode.Err, Messages.CantFlyHere);
        event.setCancelled(true);
        Location underLocation = player.getLocation().add(0, -1, 0); 
        Material underType = underLocation.getBlock().getType();
        if(!underType.isSolid()) player.teleport(underLocation);
        player.setFallDistance(player.getFallDistance() + 1);
    }
    
    public FlagDef_NoFlight(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "NoFlight";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNoFlight);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNoFlight);
    }
}
