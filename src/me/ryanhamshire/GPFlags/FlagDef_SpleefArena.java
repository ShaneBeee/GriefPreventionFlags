package me.ryanhamshire.GPFlags;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import me.ryanhamshire.GriefPrevention.events.PreventBlockBreakEvent;

public class FlagDef_SpleefArena extends FlagDefinition
{
    ConcurrentHashMap<UUID, Location> respawnMap = new ConcurrentHashMap<UUID, Location>();
    
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Location location = player.getLocation();
        
        Flag flag = this.GetFlagInstanceAtLocation(location, player);
        if(flag == null) return;
        
        SpleefData data = new SpleefData(flag.getParametersArray());
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, playerData.lastClaim);
        if(claim == null) return;
        
        ArrayList<Chunk> chunks = claim.getChunks();
        for(Chunk chunk : chunks)
        {
            for(int x = 0; x < 16; x++)
            {
                for(int z = 0; z < 16; z++)
                {
                    for(int y = 0; y < location.getWorld().getMaxHeight() - data.differenceY; y++)
                    {
                        if(claim.contains(location, true, false))
                        {
                            Block block = chunk.getBlock(x,  y,  z);
                            if(data.IsSupport(block))
                            {
                                chunk.getBlock(x, y + data.differenceY, z).setTypeIdAndData(data.blockID, data.blockData == null ? (byte)0 : (byte)(int)data.blockData, false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPreventBlockBreak(PreventBlockBreakEvent event)
    {
        Block block = event.getInnerEvent().getBlock();
        Location location = block.getLocation();
        
        Flag flag = this.GetFlagInstanceAtLocation(location, null);
        if(flag == null) return;
        
        SpleefData data = new SpleefData(flag.getParametersArray());
        if(data.IsBlock(block))
        {
            event.setCancelled(true);  //break the block
            block.getDrops().clear();  //but don't drop anything
        }
    }
    
    public FlagDef_SpleefArena(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        String [] params = parameters.split(" ");
        
        if(params.length != 3)
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.SpleefArenaHelp));
        }
        
        try
        {
            if(params[0].contains(":"))
            {
                String [] params_2 = params[0].split(":");
                Integer.valueOf(params_2[0]);
                Integer.valueOf(params_2[1]);
            }
            else
            {
                Integer.valueOf(params[0]);
            }
            
            if(params[1].contains(":"))
            {
                String [] params_2 = params[1].split(":");
                Integer.valueOf(params_2[0]);
                Integer.valueOf(params_2[1]);
            }
            else
            {
                Integer.valueOf(params[1]);
            }
            
            Integer.valueOf(params[2]);
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
        return "SpleefArena";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.SetSpleefArena);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.UnSetSpleefArena);
    }
    
    private class SpleefData
    {
        Integer supportID = null;
        Integer supportData = null;
        Integer blockID = null;
        Integer blockData = null;
        Integer differenceY = null;
        
        SpleefData(String [] params)
        {
            if(params[0].contains(":"))
            {
                String [] params_2 = params[0].split(":");
                blockID = Integer.valueOf(params_2[0]);
                blockData = Integer.valueOf(params_2[1]);
            }
            else
            {
                blockID = Integer.valueOf(params[0]);
            }
            
            if(params[1].contains(":"))
            {
                String [] params_2 = params[1].split(":");
                supportID = Integer.valueOf(params_2[0]);
                supportData = Integer.valueOf(params_2[1]);
            }
            else
            {
                supportID = Integer.valueOf(params[1]);
            }
            
            differenceY = Integer.valueOf(params[2]);
        }
        
        @SuppressWarnings("deprecation")
        boolean IsSupport(Block b)
        {
            return b.getTypeId() == supportID && (supportData == null || supportData == b.getData());
        }
        
        @SuppressWarnings("deprecation")
        boolean IsBlock(Block b)
        {
            return b.getTypeId() == blockID && (blockData == null || blockData == b.getData());
        }
    }
}
