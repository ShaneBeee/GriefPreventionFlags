package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.message.Message;
import me.ryanhamshire.GPFlags.message.Messages;
import me.ryanhamshire.GPFlags.util.VersionControl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoPlayerDamageByMonster extends FlagDefinition {

    private VersionControl vc;

    public FlagDef_NoPlayerDamageByMonster(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
        this.vc = plugin.getVersionControl();
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!vc.isMonster(damager) && !(damager instanceof Projectile)) return;
        Entity victim = event.getEntity();
        if (!(victim instanceof Player)) return;
        Flag flag = this.GetFlagInstanceAtLocation(victim.getLocation(), null);
        if (flag == null) return;
        if (damager instanceof Projectile) {
            Projectile projectile = ((Projectile) damager);
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) return;
            if (shooter instanceof Mob) {
                ((Mob) shooter).setTarget(null);
            }
        }

        event.setCancelled(true);
        if (damager instanceof Mob) {
            ((Mob) damager).setTarget(null);
        }
    }

    @EventHandler
    private void onPoison(EntityPotionEffectEvent event) {
        if (event.getCause() != EntityPotionEffectEvent.Cause.ATTACK) return;
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Flag flag = this.GetFlagInstanceAtLocation(entity.getLocation(), null);
            if (flag == null) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onTarget(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        if (!(target instanceof Player)) return;
        if (!vc.isMonster(event.getEntity())) return;

        Flag flag = this.GetFlagInstanceAtLocation(target.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
        event.setTarget(null);
    }

    @Override
    public String getName() {
        return "NoPlayerDamageByMonster";
    }

    @Override
    public Message getSetMessage() {
        return Messages.NO_PLAYER_DAMAGE_MONSTER_ENABLE;
    }

    @Override
    public Message getUnSetMessage() {
        return Messages.NO_PLAYER_DAMAGE_MONSTER_DISABLE;
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}