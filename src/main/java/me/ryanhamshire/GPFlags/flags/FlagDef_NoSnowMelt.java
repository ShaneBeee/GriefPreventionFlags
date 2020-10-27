package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFadeEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoSnowMelt extends FlagDefinition {

    public FlagDef_NoSnowMelt(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowth(BlockFadeEvent event) {
        Block block = event.getBlock();

        Flag flag = this.getFlagInstanceAtLocation(block.getLocation(), null);
        if (flag == null) return;

        if (event.getBlock().getType() != Material.SNOW) return;
        event.setCancelled(true);
    }

    @Override
    public String getName() {
        return "NoSnowMelt";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoSnowMelt);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoSnowMelt);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
