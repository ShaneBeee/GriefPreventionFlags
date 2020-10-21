package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
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
        }

        Player player = (Player) damager;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entity.getLocation(), false, null);
        if (claim.getPermission(player.getUniqueId().toString()) == ClaimPermission.Inventory) return;
        event.setCancelled(true);
    }

    public FlagDef_ProtectNamedMobs(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
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