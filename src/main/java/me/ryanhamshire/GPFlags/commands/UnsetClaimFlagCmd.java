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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class UnsetClaimFlagCmd extends BaseCmd {

    UnsetClaimFlagCmd(GPFlags plugin) {
        super(plugin);
        command = "UnsetClaimFlag";
        usage = "<flag>";
        requirePlayer = true;
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) return false;

        Player player = ((Player) sender);

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        if (claim == null) {
            Util.sendMessage(player, TextMode.Err, Messages.StandInAClaim);
            return true;
        }

        Long claimID = claim.getID();
        if (claimID == null || claimID == -1) {
            Util.sendMessage(player, TextMode.Err, Messages.UpdateGPForSubdivisionFlags);
            return true;
        }
        String flagName = args[0];

        FlagDefinition def = PLUGIN.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(player, TextMode.Err, getFlagDefsMessage(player));
            return true;
        }

        if (!playerHasPermissionForFlag(def, player)) {
            Util.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
            return true;
        }

        if (claim.allowEdit(player) != null) {
            Util.sendMessage(player, TextMode.Err, Messages.NotYourClaim);
            return true;
        }

        // TODO RESET BIOME
        if (flagName.equalsIgnoreCase("ChangeBiome")) {
            FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) PLUGIN.getFlagManager().getFlagDefinitionByName("changebiome"));
            flagD.resetBiome(claim.getID());
        }

        SetFlagResult result = PLUGIN.getFlagManager().unSetFlag(claimID.toString(), def);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        Util.sendMessage(player, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        if (result.isSuccess()) PLUGIN.getFlagManager().save();

        return true;
    }

    @Override
    List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return flagTab(sender, args[0]);
        }
        return Collections.emptyList();
    }
}
