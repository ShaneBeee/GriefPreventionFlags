package me.ryanhamshire.GPFlags.flags;


import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.WorldSettings;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_PlayerGamemode extends PlayerMovementFlagDefinition implements Listener {

    public FlagDef_PlayerGamemode(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to) {
        WorldSettings settings = this.settingsManager.get(player.getWorld());

        if (lastLocation == null) return true;
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) {
            if (this.getFlagInstanceAtLocation(lastLocation, player) == null) return true;

            String gameMode = settings.worldGamemodeDefault;
            player.setGameMode(GameMode.valueOf(gameMode.toUpperCase()));
            Util.sendMessage(player, TextMode.Warn, Messages.PlayerGamemode, gameMode);

            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                Block block = player.getLocation().getBlock();
                Block below = block.getRelative(BlockFace.DOWN);
                if (below.getRelative(BlockFace.DOWN).getType() != Material.AIR && block.getRelative(BlockFace.UP).getType() == Material.AIR)
                    return true;
                while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.teleport(block.getRelative(BlockFace.UP).getLocation());
            }
            return true;
        }
        if (flag == this.getFlagInstanceAtLocation(lastLocation, player)) return true;
        String gameMode = flag.parameters;
        String playerGameMode = player.getGameMode().toString();
        if (gameMode.equalsIgnoreCase(playerGameMode)) return true;
        player.setGameMode(GameMode.valueOf(gameMode.toUpperCase()));
        Util.sendMessage(player, TextMode.Warn, Messages.PlayerGamemode, gameMode);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag != null) {
            String gameMode = flag.parameters;
            player.setGameMode(GameMode.valueOf(gameMode.toUpperCase()));
        }
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerGamemodeRequired));
        }
        if (!parameters.equalsIgnoreCase("survival") && !parameters.equalsIgnoreCase("creative") &&
                !parameters.equalsIgnoreCase("adventure") && !parameters.equalsIgnoreCase("spectator")) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerGamemodeRequired));
        }
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public String getName() {
        return "PlayerGamemode";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.PlayerGamemodeSet, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.PlayerGamemodeUnSet);
    }

}
