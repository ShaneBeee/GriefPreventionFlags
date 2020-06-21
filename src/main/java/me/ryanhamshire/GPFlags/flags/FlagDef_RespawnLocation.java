package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.message.Message;
import me.ryanhamshire.GPFlags.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlagDef_RespawnLocation extends FlagDefinition {

    private ConcurrentHashMap<UUID, Location> respawnMap = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();

        Flag flag = this.GetFlagInstanceAtLocation(location, player);
        if (flag == null) return;

        String[] params = flag.getParametersArray();
        World respawnWorld = Bukkit.getServer().getWorld(params[0]);
        Location respawnLocation = new Location(respawnWorld, Double.valueOf(params[1]), Double.valueOf(params[2]),
                Double.valueOf(params[3]), params.length < 5 ? 0 : Float.valueOf(params[4]),
                params.length < 6 ? 0 : Float.valueOf(params[5]));

        this.respawnMap.put(player.getUniqueId(), respawnLocation);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location respawnLocation = this.respawnMap.get(player.getUniqueId());
        if (respawnLocation != null) {
            event.setRespawnLocation(respawnLocation);
            this.respawnMap.remove(player.getUniqueId());
        }
    }

    public FlagDef_RespawnLocation(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters) {
        String[] params = parameters.split(" ");

        if (params.length < 4) {
            return new SetFlagResult(false, Messages.LOCATION_REQUIRED);
        }

        World world = Bukkit.getWorld(params[0]);
        if (world == null) {
            return new SetFlagResult(false, Messages.WORLD_NOT_FOUND);
        }

        try {
            Double.valueOf(params[1]);
            Double.valueOf(params[2]);
            Double.valueOf(params[3]);
            if (params.length > 4) Float.valueOf(params[4]);
            if (params.length > 5) Float.valueOf(params[5]);
        } catch (NumberFormatException e) {
            return new SetFlagResult(false, Messages.LOCATION_REQUIRED);
        }

        return new SetFlagResult(true, this.getSetMessage(), parameters);
    }

    @Override
    public String getName() {
        return "RespawnLocation";
    }

    @Override
	public Message getSetMessage() {
        return Messages.RESPAWN_LOCATION_SET;
    }

    @Override
    public Message getUnSetMessage() {
        return Messages.RESPAWN_LOCATION_UNSET;
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
