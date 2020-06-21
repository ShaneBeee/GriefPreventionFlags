package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.message.Message;
import me.ryanhamshire.GPFlags.message.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoItemPickup extends FlagDefinition {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());

            Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
            if (flag == null) return;

            event.setCancelled(true);
        }
    }

    public FlagDef_NoItemPickup(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "NoItemPickup";
    }

    @Override
	public Message getSetMessage() {
        return Messages.NO_ITEM_PICKUP_ENABLE;
    }

    @Override
    public Message getUnSetMessage() {
        return Messages.NO_ITEM_PICKUP_DISABLE;
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.SERVER, FlagType.WORLD);
    }

}
