package me.ivehydra.customdrops;

import me.ivehydra.customdrops.action.ActionManager;
import me.ivehydra.customdrops.commands.CustomDropsCommands;
import me.ivehydra.customdrops.commands.CustomDropsTabCompleter;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.file.FileManager;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.listeners.*;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class CustomDrops extends JavaPlugin {

    private static CustomDrops instance;
    private FileManager fileManager;
    private ActionManager actionManager;
    private CustomDropManager customDropManager;
    private Map<UUID, String> mythicEntities;
    private List<UUID> naturalEntities;
    private List<UUID> spawnerEntities;
    private List<UUID> spawnerEggEntities;
    private Map<String, PlayerGUI> playerGUIMap;
    private String latestVersion = null;

    @Override
    public void onEnable() {
        instance = this;
        fileManager = new FileManager();
        mythicEntities = new HashMap<>();
        naturalEntities = new ArrayList<>();
        spawnerEntities = new ArrayList<>();
        spawnerEggEntities = new ArrayList<>();
        playerGUIMap = new HashMap<>();

        if(isPluginPresent("PlaceholderAPI")) sendLog("[CustomDrops]" + ChatColor.GREEN + " PlaceholderAPI has been found. Now you can use PlaceholderAPI placeholders for Conditions and Actions.");
        else sendLog("[CustomDrops]" + ChatColor.YELLOW + " PlaceholderAPI not found. The plugin will still function correctly, but you won't be able to use PlaceholderAPI placeholders for Conditions and Actions.");

        if(isPluginPresent("MythicMobs")) sendLog("[CustomDrops]" + ChatColor.GREEN + " MythicMobs has been found. Now you can set Custom Drops for Custom Entities.");

        registerConfigFile();
        fileManager.createFile("drops", "blocks.yml");
        fileManager.createFile("drops", "entities.yml");
        fileManager.createFile("drops", "fishing.yml");
        fileManager.createFile("drops", "piglinbartering.yml");

        actionManager = new ActionManager();
        customDropManager = new CustomDropManager();

        registerCommands();
        registerListeners();

        updateChecker(version -> {
            String currentVersion = getDescription().getVersion();
            if(currentVersion.equals(version)) sendLog(MessageUtils.LATEST_VERSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%current_version%", currentVersion, "%new_version%", version));
            else instance.getConfig().getStringList(MessageUtils.NEW_VERSION.getPath()).forEach(message -> sendLog(StringUtils.getColoredString(message).replace("%prefix%", MessageUtils.PREFIX.toString()).replace("%current_version%", currentVersion).replace("%new_version%", version)));
        });
    }

    @Override
    public void onDisable() {
        instance = null;

        fileManager.saveAll();

    }

    public static CustomDrops getInstance() { return instance; }

    public FileManager getFileManager() { return fileManager; }

    public boolean isPluginPresent(String name) { return Bukkit.getPluginManager().getPlugin(name) != null; }

    public Map<UUID, String> getMythicEntities() { return mythicEntities; }

    public List<UUID> getNaturalEntities() { return naturalEntities; }

    public List<UUID> getSpawnerEntities() { return spawnerEntities; }

    public List<UUID> getSpawnerEggEntities() { return spawnerEggEntities; }

    public PlayerGUI getPlayerGUI(Player p) {
        String name = p.getName();
        if(playerGUIMap.containsKey(name)) return playerGUIMap.get(name);
        PlayerGUI playerGUI = new PlayerGUI(p);
        playerGUIMap.put(name, playerGUI);
        return playerGUI;
    }

    public void removePlayerGUI(Player p) { playerGUIMap.remove(p.getName()); }

    public String getLatestVersion() { return latestVersion; }

    private void registerConfigFile() {
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) saveResource("config.yml", false);
        File config = new File(getDataFolder(), "config.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(config);
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(getResource("config.yml")), StandardCharsets.UTF_8);
        YamlConfiguration yamlReader = YamlConfiguration.loadConfiguration(reader);
        for(String string : yamlReader.getKeys(true))
            if(!yamlConfig.contains(string)) yamlConfig.set(string, yamlReader.get(string));
        try {
            yamlConfig.save(config);
        } catch(IOException e) {
            sendLog("[CustomDrops]" + ChatColor.RED + " An error occurred while trying to save the configuration file.");
            sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    public void reload() {
        reloadConfig();
        reloadCustomDropManager();
    }

    public void reloadCustomDropManager() {
        fileManager.reloadAll();
        customDropManager = new CustomDropManager();
    }

    public ActionManager getActionManager() { return actionManager; }

    public CustomDropManager getCustomDropManager() { return customDropManager; }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("customdrops")).setExecutor(new CustomDropsCommands());
        Objects.requireNonNull(getCommand("customdrops")).setTabCompleter(new CustomDropsTabCompleter());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);
        if(isPluginPresent("MythicMobs"))
            pm.registerEvents(new MythicMobSpawnListener(), this);
        pm.registerEvents(new EntityDeathListener(), this);
        pm.registerEvents(new CreatureSpawnListener(), this);
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new InventoryCloseListener(), this);
        pm.registerEvents(new PlayerFishListener(), this);
        if(VersionUtils.isAtLeastVersion116())
            pm.registerEvents(new PiglinBarterListener(), this);
    }

    private void updateChecker(Consumer<String> consumer) {
        if(!instance.getConfig().getBoolean("updateCheck")) return;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try(InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=132266").openStream()) {
                Scanner scanner = new Scanner(stream);
                if(scanner.hasNext()) {
                    String version = scanner.next();
                    latestVersion = version;
                    consumer.accept(version);
                }
            } catch(IOException e) {
                sendLog("[CustomDrops]" + ChatColor.RED + " Can't find a new version!");
                sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        });
    }

    public void sendLog(String string) { getServer().getConsoleSender().sendMessage(string); }

}
