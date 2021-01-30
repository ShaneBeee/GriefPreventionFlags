package me.ryanhamshire.GPFlags;

import com.google.common.io.Files;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for flags
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FlagManager {

    private final ConcurrentHashMap<String, FlagDefinition> definitions;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Flag>> flags;
    private final List<String> worlds = new ArrayList<>();

    public static final String DEFAULT_FLAG_ID = "-2";

    public FlagManager() {
        this.definitions = new ConcurrentHashMap<>();
        this.flags = new ConcurrentHashMap<>();
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
    }

    /**
     * Register a new flag definition
     *
     * @param def Flag Definition to register
     */
    public void registerFlagDefinition(FlagDefinition def) {
        String name = def.getName();
        this.definitions.put(name.toLowerCase(), def);
    }

    /**
     * Get a flag definition by name
     *
     * @param name Name of the flag to get
     * @return Flag definition by name
     */
    public FlagDefinition getFlagDefinitionByName(String name) {
        return this.definitions.get(name.toLowerCase());
    }

    /**
     * Get a collection of all registered flag definitions
     *
     * @return All registered flag definitions
     */
    public Collection<FlagDefinition> getFlagDefinitions() {
        return new ArrayList<>(this.definitions.values());
    }

    /**
     * Get a collection of names of all registered flag definitions
     *
     * @return Names of all registered flag definitions
     */
    public Collection<String> getFlagDefinitionNames() {
        return new ArrayList<>(this.definitions.keySet());
    }

    /**
     * Set a flag for a claim
     *
     * @param claim    {@link Claim} which this flag will be attached to
     * @param def      Flag definition to set
     * @param isActive Whether the flag will be active or not
     * @param args     Message parameters
     * @return Result of setting flag
     */
    public SetFlagResult setFlag(Claim claim, FlagDefinition def, boolean isActive, String... args) {
        return setFlag(claim.getID().toString(), def, isActive, args);
    }

    /**
     * Set a flag for a claim
     *
     * @param claimId  ID of {@link Claim} which this flag will be attached to
     * @param def      Flag definition to set
     * @param isActive Whether the flag will be active or not
     * @param args     Message parameters
     * @return Result of setting flag
     */
    public SetFlagResult setFlag(String claimId, FlagDefinition def, boolean isActive, String... args) {
        StringBuilder parameters = new StringBuilder();
        for (String arg : args) {
            parameters.append(arg).append(" ");
        }
        parameters = new StringBuilder(parameters.toString().trim());

        SetFlagResult result;
        if (isActive) {
            result = def.validateParameters(parameters.toString());
            if (!result.success) return result;
        } else {
            result = new SetFlagResult(true, def.getUnSetMessage());
        }

        Flag flag = new Flag(def, parameters.toString());
        flag.setSet(isActive);
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimId);
        if (claimFlags == null) {
            claimFlags = new ConcurrentHashMap<>();
            this.flags.put(claimId, claimFlags);
        }

        String key = def.getName().toLowerCase();
        if (!claimFlags.containsKey(key) && isActive) {
            def.incrementInstances();
        }
        claimFlags.put(key, flag);
        return result;
    }

    /**
     * Get a registered flag in a claim
     *
     * @param claim   Claim to get a flag from
     * @param flagDef Flag definition to get
     * @return Instance of flag
     */
    public Flag getFlag(Claim claim, FlagDefinition flagDef) {
        if (claim == null || flagDef == null) return null;
        return getFlag(claim.getID().toString(), flagDef);
    }

    /**
     * Get a registered/default flag in a claim
     *
     * @param claimID ID of claim
     * @param flagDef Flag definition to get
     * @return Instance of flag
     */
    public Flag getFlag(String claimID, FlagDefinition flagDef) {
        if (claimID == null || flagDef == null) return null;
        return this.getFlag(claimID, flagDef.getName());
    }

    /**
     * Get a registered/default flag in a claim
     *
     * @param claim Claim to get a flag from
     * @param flag  Name of flag definition to get
     * @return Instance of flag
     */
    public Flag getFlag(Claim claim, String flag) {
        if (claim == null || flag == null) return null;
        return getFlag(claim.getID().toString(), flag);
    }

    /**
     * Get a registered/default flag in a claim
     *
     * @param claimID ID of claim
     * @param flag    Name of flag definition to get
     * @return Instance of flag
     */
    public Flag getFlag(String claimID, String flag) {
        if (claimID == null || flag == null) return null;
        String flagString = flag.toLowerCase(Locale.ROOT);
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
        if (claimFlags != null) {
            if (claimFlags.containsKey(flagString)) {
                return claimFlags.get(flagString);
            }
        }
        if (claimID.equalsIgnoreCase("everywhere") || worlds.contains(claimID)) return null;
        ConcurrentHashMap<String, Flag> defaultFlags = this.flags.get(DEFAULT_FLAG_ID);
        if (defaultFlags != null) {
            if (defaultFlags.containsKey(flagString)) {
                return defaultFlags.get(flagString);
            }
        }
        return null;
    }

    /**
     * Get all flags in a claim
     *
     * @param claim Claim to get flags from
     * @return All flags in this claim
     */
    public Collection<Flag> getFlags(Claim claim) {
        if (claim == null) return null;
        return getFlags(claim.getID().toString());
    }

    /**
     * Get all flags in a claim
     *
     * @param claimID ID of claim
     * @return All flags in this claim
     */
    public Collection<Flag> getFlags(String claimID) {
        if (claimID == null) return null;
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
        if (claimFlags == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(claimFlags.values());
        }
    }

    /**
     * Unset a flag in a claim
     *
     * @param claim Claim to remove flag from
     * @param def   Flag definition to remove
     * @return Flag result
     */
    public SetFlagResult unSetFlag(Claim claim, FlagDefinition def) {
        return unSetFlag(claim.getID().toString(), def);
    }

    /**
     * Unset a flag in a claim
     *
     * @param claimID ID of claim
     * @param def     Flag definition to remove
     * @return Flag result
     */
    public SetFlagResult unSetFlag(String claimID, FlagDefinition def) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
        if (claimFlags == null || !claimFlags.containsKey(def.getName().toLowerCase())) {
            return this.setFlag(claimID, def, false);
        } else {
            claimFlags.remove(def.getName().toLowerCase());
            return new SetFlagResult(true, def.getUnSetMessage());
        }
    }

    List<MessageSpecifier> load(String input) throws InvalidConfigurationException {
        this.flags.clear();
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.loadFromString(input);

        ArrayList<MessageSpecifier> errors = new ArrayList<>();
        Set<String> claimIDs = yaml.getKeys(false);
        for (String claimID : claimIDs) {
            Set<String> flagNames = yaml.getConfigurationSection(claimID).getKeys(false);
            for (String flagName : flagNames) {
                String paramsDefault = yaml.getString(claimID + "." + flagName);
                String params = yaml.getString(claimID + "." + flagName + ".params", paramsDefault);
                boolean set = yaml.getBoolean(claimID + "." + flagName + ".value", true);
                FlagDefinition def = this.getFlagDefinitionByName(flagName);
                if (def != null) {
                    SetFlagResult result = this.setFlag(claimID, def, set, params);
                    if (!result.success) {
                        errors.add(result.message);
                    }
                }
            }
        }
        return errors;
    }

    public void save() {
        try {
            this.save(FlagsDataStore.flagsFilePath);
        } catch (Exception e) {
            Util.log("Failed to save flag data.  Details:");
            e.printStackTrace();
        }
    }

    public String flagsToString() {
        YamlConfiguration yaml = new YamlConfiguration();

        Set<String> claimIDs = this.flags.keySet();
        for (String claimID : claimIDs) {
            ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
            Set<String> flagNames = claimFlags.keySet();
            for (String flagName : flagNames) {
                Flag flag = claimFlags.get(flagName);
                String paramsPath = claimID + "." + flagName + ".params";
                yaml.set(paramsPath, flag.parameters);
                String valuePath = claimID + "." + flagName + ".value";
                yaml.set(valuePath, flag.getSet());
            }
        }

        return yaml.saveToString();
    }

    public void save(String filepath) throws IOException {
        String fileContent = this.flagsToString();
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        Files.write(fileContent.getBytes("UTF-8"), file);
    }

    public List<MessageSpecifier> load(File file) throws IOException, InvalidConfigurationException {
        if (!file.exists()) return this.load("");

        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line).append('\n');
        }

        return this.load(builder.toString());
    }

    public void clear() {
        this.flags.clear();
    }

    void removeExceptClaimIDs(HashSet<String> validClaimIDs) {
        HashSet<String> toRemove = new HashSet<>();
        for (String key : this.flags.keySet()) {
            //if not a valid claim ID (maybe that claim was deleted)
            if (!validClaimIDs.contains(key)) {
                try {
                    int numericalValue = Integer.parseInt(key);

                    //if not a special value like default claims ID, remove
                    if (numericalValue >= 0) toRemove.add(key);
                } catch (NumberFormatException ignore) {
                } //non-numbers represent server or world flags, so ignore those
            }
        }
        for (String key : toRemove) {
            this.flags.remove(key);
        }
        save();
    }

}
