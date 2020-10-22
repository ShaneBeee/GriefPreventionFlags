package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_ProtectNamedMobs extends FlagDefinition {

    public FlagDef_ProtectNamedMobs(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Flag flag = this.GetFlagInstanceAtLocation(entity.getLocation(), null);
        if (flag == null) return;
        if (entity.getType() == EntityType.PLAYER) return;
        if (entity.getCustomName() == null) return;

        Entity damager = event.getDamager();
        if (damager.getType() != EntityType.PLAYER) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) damager;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entity.getLocation(), false, null);
        if (claim.getPermission(player.getUniqueId().toString()) == ClaimPermission.Inventory) return;
        event.setCancelled(true);
    }

    @Override
    public String getName() {
        return "ProtectNamedMobs";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledProtectNamedMobs);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledProtectNamedMobs);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
