package me.ryanhamshire.GPFlags.flags;

import com.gmail.nossr50.events.hardcore.McMMOPlayerDeathPenaltyEvent;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.message.Message;
import me.ryanhamshire.GPFlags.message.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMcMMODeathPenalty extends FlagDefinition {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDisarm(McMMOPlayerDeathPenaltyEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag != null) {
            event.setCancelled(true);
        }
    }

    public FlagDef_NoMcMMODeathPenalty(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "NoMcMMODeathPenalty";
    }

    @Override
	public Message getSetMessage() {
        return Messages.NO_MCMMO_PENALTY_ENABLE;
    }

    @Override
    public Message getUnSetMessage() {
        return Messages.NO_MCMMO_PENALTY_DISABLE;
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
