package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandHandler implements TabExecutor {

    private final Map<String, BaseCmd> COMMANDS = new HashMap<>();
    private final GPFlags PLUGIN;
    private final PluginManager PM = Bukkit.getPluginManager();

    @SuppressWarnings("ConstantConditions")
    public CommandHandler(GPFlags plugin) {
        this.PLUGIN = plugin;
        plugin.getCommand("gpflags").setExecutor(this);
        registerCommands();
        registerFlagPermissions();
    }

    private void registerCommands() {
        registerCommand(AllFlagsCmd.class);
        registerCommand(ListClaimFlagsCmd.class);
        registerCommand(ReloadCmd.class);
        registerCommand(SetClaimFlagCmd.class);
        registerCommand(SetClaimFlagPlayerCmd.class);
        registerCommand(SetDefaultClaimFlagCmd.class);
        registerCommand(SetServerFlagCmd.class);
        registerCommand(SetWorldFlagCmd.class);
        registerCommand(UnsetClaimFlagCmd.class);
        registerCommand(UnsetClaimFlagPlayerCmd.class);
        registerCommand(UnsetDefaultClaimFlagCmd.class);
        registerCommand(UnsetServerFlagCmd.class);
        registerCommand(UnsetWorldFlagCmd.class);
    }

    private void registerFlagPermissions() {
        PLUGIN.getFlagManager().getFlagDefinitions().forEach(flagDefinition -> {
            String name = flagDefinition.getName();
            String permName = name.toLowerCase(Locale.ROOT);
            Permission perm = new Permission("gpflags.flag." + permName);
            perm.setDescription(String.format("Grants permission to use GriefPreventFlags flag '%s'", name));
            perm.setDefault(PermissionDefault.OP);
            if (PM.getPermission(perm.getName()) == null) {
                PM.addPermission(perm);
            }
        });
    }

    private void registerCommand(Class<? extends BaseCmd> commandClass) {
        try {
            Constructor<? extends BaseCmd> constructor = commandClass.getDeclaredConstructor(GPFlags.class);
            BaseCmd cmd = constructor.newInstance(PLUGIN);
            String cmdName = cmd.command.toLowerCase(Locale.ROOT);
            COMMANDS.put(cmdName, cmd);
            String permName = "gpflags.command." + cmdName;
            Permission perm = new Permission(permName);
            perm.setDescription(String.format("Grants permission to use GriefPreventFlags command '%s'", cmd.command));
            perm.setDefault(PermissionDefault.OP);
            if (PM.getPermission(permName) == null) {
                PM.addPermission(perm);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            String commandString = args[0].toLowerCase(Locale.ROOT);
            if (COMMANDS.containsKey(commandString)) {
                if (hasPermission(sender, commandString)) {
                    String[] commandArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, commandArgs, 0, args.length - 1);
                    BaseCmd command = COMMANDS.get(commandString);

                    // If console tries to use a player only command, we exit
                    if (!(sender instanceof Player) && command.requirePlayer) {
                        Util.sendMessage(sender, TextMode.Warn, Messages.PlayerOnlyCommand, commandString);
                        return true;
                    }
                    // Execute command and look for success
                    if (!command.execute(sender, commandArgs)) {
                        Util.sendMessage(sender, command.getUsage());
                    }
                } else {
                    Util.sendMessage(sender, TextMode.Err, Messages.NoCommandPermission, commandString);
                }
            } else {
                Util.sendMessage(sender, TextMode.Warn, Messages.UnknownCommand, commandString);
            }
        } else {
            Util.sendMessage(sender, "&6Command usage: &3&l/gpflags &r<&bcommand&r>");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add("<flag> [<parameters>]");
            COMMANDS.values().forEach(command -> {
                if (hasPermission(sender, command.command)) {
                    if (!(sender instanceof Player) && command.requirePlayer) {
                        return;
                    }
                    commands.add(command.command);
                }
            });
            return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
        } else if (args.length > 1) {
            String commandString = args[0].toLowerCase(Locale.ROOT);
            if (COMMANDS.containsKey(commandString)) {
                String[] commandArgs = new String[args.length - 1];
                System.arraycopy(args, 1, commandArgs, 0, args.length - 1);
                BaseCmd command = COMMANDS.get(commandString);
                if (!(sender instanceof Player) && command.requirePlayer) {
                    return Collections.emptyList();
                }
                return command.tab(sender, commandArgs);
            }
        }
        return null;
    }

    // TODO (dec 25) remove "gpflags.(command)" permission, use "gpflags.command.(command)" instead
    private boolean hasPermission(CommandSender sender, String command) {
        return sender.hasPermission("gpflags." + command) || sender.hasPermission("gpflags.command." + command);
    }

}
