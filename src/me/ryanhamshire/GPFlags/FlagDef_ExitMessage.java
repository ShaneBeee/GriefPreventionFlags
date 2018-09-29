package me.ryanhamshire.GPFlags;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

public class FlagDef_ExitMessage extends PlayerMovementFlagDefinition
{
    @Override
    public boolean allowMovement(Player player, Location lastLocation)
    {
        Location to = player.getLocation();
        if(lastLocation == null) return true;
        Flag flag = this.GetFlagInstanceAtLocation(lastLocation, player);
        if(flag == null) return true;
        
        if(flag == this.GetFlagInstanceAtLocation(to, player)) return true;
        
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String message = flag.parameters;
        if(playerData.lastClaim != null)
        {
            message = message.replace("%owner%", playerData.lastClaim.getOwnerName()).replace("%name%", player.getName());
        }
        
        GPFlags.sendMessage(player, TextMode.Info, message);
        
        return true;
    }
    
    public FlagDef_ExitMessage(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "ExitMessage";
    }

    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.MessageRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }
    
    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.AddedExitMessage, parameters);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.RemovedExitMessage);
    }
}
