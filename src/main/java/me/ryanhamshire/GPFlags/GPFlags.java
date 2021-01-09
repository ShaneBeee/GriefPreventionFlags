package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GPFlags.listener.PlayerListener;
import me.ryanhamshire.GPFlags.metrics.Metrics;
import me.ryanhamshire.GPFlags.util.Current;
import me.ryanhamshire.GPFlags.util.Legacy;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GPFlags.util.VersionControl;
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
    private VersionControl vc;
    private CommandHandler oldCommandHandler;
    private FlagsDataStore flagsDataStore;
    private final FlagManager flagManager = new FlagManager();
    private WorldSettingsManager worldSettingsManager;

    boolean registeredFlagDefinitions = false;
    private PlayerListener playerListener;

    static boolean LOG_ENTER_EXIT_COMMANDS = true;

    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        this.playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);

        // Check if server is running MC 1.13+ (API Changes)
        if (Util.isRunningMinecraft(1, 13)) {
            vc = new Current();
            addLogEntry(ChatColor.GREEN + "1.13+ Version Loaded");
        } else {
            vc = new Legacy();
            addLogEntry(ChatColor.GREEN + "Legacy Version Loaded");
        }

        this.flagsDataStore = new FlagsDataStore();
        reloadConfig();
        // Old command handler
        // TODO remove after a while, let people get used to the new ones first
        this.oldCommandHandler = new CommandHandler(this);
        // New command handler
        new me.ryanhamshire.GPFlags.commands.CommandHandler(this);

        new Metrics(this);

        float finish = (float) (System.currentTimeMillis() - start) / 1000;
        addLogEntry("Successfully loaded in &b%.2f seconds", finish);
        if (getDescription().getVersion().contains("Beta")) {
            addLogEntry("&eYou are running a Beta version, things may not operate as expected");
        }
    }

    public void onDisable() {
        this.flagsDataStore.close();
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
     * Log a message to console
     *
     * @param entry Message to log
     */
    public static synchronized void addLogEntry(String entry) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bGP&3Flags&7] " + entry));
    }

    /**
     * Log a formatted message to console
     *
     * @param format Message format
     * @param objects Objects in format
     */
    public static synchronized void addLogEntry(String format, Object... objects) {
        addLogEntry(String.format(format, objects));
    }

    public static void logFlagCommands(String log) {
        if (LOG_ENTER_EXIT_COMMANDS) {
            addLogEntry(log);
        }
    }

    /**
     * Send a {@link MessageSpecifier} to a player, or console if player is null
     *
     * @param player    Player to send message to, or null if to console
     * @param color     Color of message
     * @param specifier Message specifier to send
     */
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
     */
    public static void sendMessage(@Nullable CommandSender player, ChatColor color, Messages messageID, String... args) {
        String message = GPFlags.instance.flagsDataStore.getMessage(messageID, args);
        sendMessage(player, color, message);
    }

    /**
     * Send a message to player, or console if player is null
     *
     * @param player  Player to send message to, or null if to console
     * @param message Message to send
     */
    static void sendMessage(@Nullable CommandSender player, @NotNull String message) {
        sendMessage(player, ChatColor.RESET, message);
    }

    /**
     * Send a message to player, or console if player is null
     *
     * @param player  Player to send message to, or null if to console
     * @param color   Color of message
     * @param message Message to send
     */
    public static void sendMessage(@Nullable CommandSender player, ChatColor color, @NotNull String message) {
        if (message.length() == 0) return;

        if (player == null) {
            GPFlags.addLogEntry(color + message);
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
     * Get an instance of the version control class
     *
     * @return Instance of the version control class
     */
    public VersionControl getVersionControl() {
        return vc;
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
