package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDef_ChangeBiome;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetClaimFlagPlayerCmd extends BaseCmd {

    SetClaimFlagPlayerCmd(GPFlags plugin) {
        super(plugin);
        command = "SetClaimFlagPlayer";
        usage = "<player> <flag> [<parameters>]";
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return false;
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Util.sendMessage(sender, "&c%s &7is not online", args[0]);
            return false;
        }
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
        if (claim == null || claim.allowEdit(player) != null) {
            Util.sendMessage(sender, "&cThis player is not standing in a claim they own");
            return false;
        }

        String flagName = args[1];
        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(sender, "&c%s&7 is not a valid flag", flagName);
            return false;
        }
        if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
            Util.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
            return true;
        }

        String[] params = new String[args.length - 2];
        System.arraycopy(args, 2, params, 0, args.length - 2);

        // SET BIOME
        if (flagName.equalsIgnoreCase("ChangeBiome")) {
            if (args.length < 3) return false;
            FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) PLUGIN.getFlagManager().getFlagDefinitionByName("changebiome"));
            String biome = params[0].toUpperCase().replace(" ", "_");
            if (!flagD.changeBiome(sender, claim, biome)) return true;
        }

        SetFlagResult result = PLUGIN.getFlagManager().setFlag(claim.getID().toString(), def, true, params);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        Util.sendMessage(sender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        if (result.isSuccess()) {
            PLUGIN.getFlagManager().save();
            Util.sendMessage(sender, "&7Flag &b%s &7successfully set in &b%s&7's claim.", def.getName(), player.getName() );
            return true;
        }
        return true;
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return null; // allows for tabbing players
        } else if (args.length == 2) {
            return flagTab(sender, args[1]);
        } else if (args.length > 2) {
            String[] params = new String[args.length - 1];
            System.arraycopy(args, 1, params, 0, args.length - 1);
            return paramTab(sender, params);
        }
        return Collections.emptyList();
    }
}
