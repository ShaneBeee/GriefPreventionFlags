package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FlagDef_HealthRegen extends TimedPlayerFlagDefinition {

    public FlagDef_HealthRegen(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public long getPlayerCheckFrequency_Ticks() {
        return 100L;
    }

    @Override
    public void processPlayer(Player player) {
        if (player.getHealth() >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() || player.isDead())
            return;

        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;

        int healAmount = 2;
        if (flag.parameters != null && !flag.parameters.isEmpty()) {
            try {
                healAmount = Integer.parseInt(flag.parameters);
            } catch (NumberFormatException e) {
                Util.log("Problem with health regen amount @ " + player.getLocation().getBlock().getLocation().toString());
            }
        }

        int newHealth = healAmount + (int) player.getHealth();
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), newHealth));
    }

    @Override
    public String getName() {
        return "HealthRegen";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableHealthRegen);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableHealthRegen);
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty())
            return new SetFlagResult(false, new MessageSpecifier(Messages.HealthRegenGreaterThanZero));

        int amount;
        try {
            amount = Integer.parseInt(parameters);
            if (amount <= 0) {
                return new SetFlagResult(false, new MessageSpecifier(Messages.HealthRegenGreaterThanZero));
            }
        } catch (NumberFormatException e) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.HealthRegenGreaterThanZero));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
