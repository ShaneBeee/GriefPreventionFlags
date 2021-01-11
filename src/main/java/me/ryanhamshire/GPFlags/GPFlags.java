package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GPFlags.listener.PlayerListener;
import me.ryanhamshire.GPFlags.metrics.Metrics;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <b>Main GriefPrevention Flags class</b>
 */
@SuppressWarnings("WeakerAccess")
public class GPFlags extends JavaPlugin {

    private static GPFlags instance;
    private CommandHandler oldCommandHandler;
    private FlagsDataStore flagsDataStore;
    private final FlagManager flagManager = new FlagManager();
    private WorldSettingsManager worldSettingsManager;

    boolean registeredFlagDefinitions = false;
    private PlayerListener playerListener;

    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        // Check if server is running MC 1.13+ (API Changes)
        // If not running 1.13+, stop the plugin
        if (!Util.isRunningMinecraft(1, 13)) {
            Util.log("&cGPFlags does not support your server version: " + Util.getMinecraftVersion());
            Util.log("&cGPFlags is only supported on 1.13+");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);

        this.flagsDataStore = new FlagsDataStore();
        reloadConfig();
        // Old command handler
        // TODO remove after a while, let people get used to the new ones first
        this.oldCommandHandler = new CommandHandler(this);
        // New command handler
        new me.ryanhamshire.GPFlags.commands.CommandHandler(this);

        new Metrics(this);

        float finish = (float) (System.currentTimeMillis() - start) / 1000;
        Util.log("Successfully loaded in &b%.2f seconds", finish);
        if (getDescription().getVersion().contains("Beta")) {
            Util.log("&eYou are running a Beta version, things may not operate as expected");
        }
    }

    public void onDisable() {
        if (flagsDataStore != null) {
            flagsDataStore.close();
            flagsDataStore = null;
        }
        instance = null;
        playerListener = null;
        oldCommandHandler = null;
    }

    /**
     * Reload the config file
     */
    public void reloadConfig() {
        this.worldSettingsManager = new WorldSettingsManager();
        new GPFlagsConfig(this);
    }

    //handles slash commands (moved to CommandHandler class)
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        return this.oldCommandHandler.onCommand(sender, cmd, commandLabel, args);
    }

    //handle tab completion in commands
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return this.oldCommandHandler.onTabComplete(sender, command, alias, args);
    }

    /**
     * Send a {@link MessageSpecifier} to a player, or console if player is null
     *
     * @param player    Player to send message to, or null if to console
     * @param color     Color of message
     * @param specifier Message specifier to send
     * @deprecated Use {@link Util#sendMessage(CommandSender, ChatColor, MessageSpecifier)}
     */
    @Deprecated // Jan 9/2020
    public static void sendMessage(@Nullable CommandSender player, ChatColor color, MessageSpecifier specifier) {
        sendMessage(player, color, specifier.messageID, specifier.messageParams);
    }

    /**
     * Send a {@link Messages Message} to a player, or console if player is null
     *
     * @param player    Player to send message to, or null if to console
     * @param color     Color of message
     * @param messageID Message to send
     * @param args      Message parameters
     * @deprecated Use {@link Util#sendMessage(CommandSender, ChatColor, Messages, String...)}
     */
    @Deprecated // Jan 9/2020
    public static void sendMessage(@Nullable CommandSender player, ChatColor color, Messages messageID, String... args) {
        String message = GPFlags.instance.flagsDataStore.getMessage(messageID, args);
        sendMessage(player, color, message);
    }

    /**
     * Send a message to player, or console if player is null
     *
     * @param player  Player to send message to, or null if to console
     * @param message Message to send
     * @deprecated Use {@link Util#sendMessage(CommandSender, String)}
     */
    @Deprecated // Jan 9/2020
    static void sendMessage(@Nullable CommandSender player, @NotNull String message) {
        sendMessage(player, ChatColor.RESET, message);
    }

    /**
     * Send a message to player, or console if player is null
     *
     * @param player  Player to send message to, or null if to console
     * @param color   Color of message
     * @param message Message to send
     * @deprecated Use {@link Util#sendMessage(CommandSender, String)}
     */
    @Deprecated // Jan 9/2020
    public static void sendMessage(@Nullable CommandSender player, ChatColor color, @NotNull String message) {
        if (message.length() == 0) return;

        if (player == null) {
            Util.log(color + message);
        } else {
            player.sendMessage(color + message);
        }
    }

    /**
     * Get an instance of this plugin
     *
     * @return Instance of this plugin
     */
    public static GPFlags getInstance() {
        return instance;
    }

    /**
     * Get an instance of the flags data store
     *
     * @return Instance of the flags data store
     */
    public FlagsDataStore getFlagsDataStore() {
        return this.flagsDataStore;
    }

    /**
     * Get an instance of the flag manager
     *
     * @return Instance of the flag manager
     */
    public FlagManager getFlagManager() {
        return this.flagManager;
    }

    /**
     * Get an instance of the world settings manager
     *
     * @return Instance of the world settings manager
     */
    public WorldSettingsManager getWorldSettingsManager() {
        return this.worldSettingsManager;
    }

    /**
     * Get an instance of the player listener class
     *
     * @return Instance of the player listener class
     */
    public PlayerListener getPlayerListener() {
        return this.playerListener;
    }

}
