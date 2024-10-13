package me.ivehydra.customdrops;

import me.ivehydra.customdrops.action.ActionManager;
import me.ivehydra.customdrops.commands.CustomDropsCommands;
import me.ivehydra.customdrops.commands.CustomDropsTabCompleter;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.listeners.*;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class CustomDrops extends JavaPlugin {

    private static CustomDrops instance;
    private File customDropsFile;
    private YamlConfiguration customDropsConfiguration = new YamlConfiguration();
    private ActionManager actionManager;
    private CustomDropManager customDropManager;
    private List<UUID> naturalEntities;
    private List<UUID> spawnerEntities;
    private List<UUID> spawnerEggEntities;
    private Map<Player, PlayerGUI> playerGUIMap;
    private Map<Player, Boolean> waiting;
    private Map<Player, Boolean> waitingC;

    @Override
    public void onEnable() {
        instance = this;
        naturalEntities = new ArrayList<>();
        spawnerEntities = new ArrayList<>();
        spawnerEggEntities = new ArrayList<>();
        playerGUIMap = new HashMap<>();
        waiting = new HashMap<>();
        waitingC = new HashMap<>();

        if(isPlaceholderAPIPresent()) sendLog("[CustomDrops]" + ChatColor.GREEN + " PlaceholderAPI has been found. Now you can use PlaceholderAPI placeholders for conditions and actions.");
        else sendLog("[CustomDrops]" + ChatColor.YELLOW + " PlaceholderAPI not found. The plugin will still function correctly, but you won't be able to use PlaceholderAPI placeholders for conditions and actions.");

        saveDefaultConfig();
        registerCustomDropsFile();

        actionManager = new ActionManager();
        customDropManager = new CustomDropManager();

        registerCommands();
        registerListeners();

        updateChecker(version -> {
            if(getDescription().getVersion().equals(version)) sendLog(MessageUtils.LATEST_VERSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            else instance.getConfig().getStringList(MessageUtils.NEW_VERSION.getPath()).forEach(message -> sendLog(StringUtils.getColoredString(message).replace("%prefix%", MessageUtils.PREFIX.toString())));
        });
    }

    @Override
    public void onDisable() {
        instance = null;

        saveCustomDropsFile();

    }

    public static CustomDrops getInstance() { return instance; }

    public boolean isPlaceholderAPIPresent() { return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null; }

    public List<UUID> getNaturalEntities() { return naturalEntities; }

    public List<UUID> getSpawnerEntities() { return spawnerEntities; }

    public List<UUID> getSpawnerEggEntities() { return spawnerEggEntities; }

    public PlayerGUI getPlayerGUI(Player p) {
        if(playerGUIMap.containsKey(p)) return playerGUIMap.get(p);
        PlayerGUI playerGUI = new PlayerGUI(p);
        playerGUIMap.put(p, playerGUI);
        return playerGUI;
    }

    public void removePlayerGUI(Player p) { playerGUIMap.remove(p); }

    public boolean getWaitingPlayer(Player p) { return waiting.get(p); }

    public boolean containsWaitingPlayer(Player p) { return waiting.containsKey(p); }

    public void addWaitingPlayer(Player p, boolean isBlock) { waiting.put(p, isBlock); }

    public void removeWaitingPlayer(Player p) { waiting.remove(p); }

    public Boolean getWaitingCPlayer(Player p) { return waitingC.get(p); }

    public boolean containsWaitingCPlayer(Player p) { return waitingC.containsKey(p); }

    public void addWaitingCPlayer(Player p, Boolean c) { waitingC.put(p, c); }

    public void removeWaitingCPlayer(Player p) { waitingC.remove(p); }

    public void reload() {
        reloadConfig();
        reloadCustomDropManager();
    }

    public void reloadCustomDropManager() {
        reloadCustomDropsFile();
        customDropManager = new CustomDropManager();
    }

    private void registerCustomDropsFile() {
        customDropsFile = new File(getDataFolder(), "customdrops.yml");
        if(!customDropsFile.exists()) saveResource("customdrops.yml", false);
        try {
            customDropsConfiguration.load(customDropsFile);
        } catch(IOException | InvalidConfigurationException e) {
            sendLog("[CustomDrops]" + ChatColor.RED + " An error occurred while trying to load 'customdrops.yml'");
            sendLog("[CustomDrops]" + " Error details: " + e.getMessage());
        }
    }

    private void reloadCustomDropsFile() {
        if(!customDropsFile.exists()) registerCustomDropsFile();
        customDropsConfiguration = YamlConfiguration.loadConfiguration(customDropsFile);
    }

    public void saveCustomDropsFile() {
        if(customDropsFile == null) return;
        try {
            getCustomDropsFile().save(customDropsFile);
        } catch(IOException e) {
            sendLog("[CustomDrops]" + ChatColor.RED + " An error occurred while trying to save 'customdrops.yml'.");
            sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    public FileConfiguration getCustomDropsFile() { return customDropsConfiguration; }

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
        pm.registerEvents(new EntityDeathListener(), this);
        pm.registerEvents(new CreatureSpawnListener(), this);
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new InventoryCloseListener(), this);
        pm.registerEvents(new PlayerFishListener(), this);
        pm.registerEvents(new AsyncPlayerChatListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);
    }

    private void updateChecker(Consumer<String> consumer) {
        if(!getConfig().getBoolean("updateCheck")) return;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try(InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=119471").openStream()) {
                Scanner scanner = new Scanner(stream);
                if(scanner.hasNext()) consumer.accept(scanner.next());
            } catch(IOException e) {
                sendLog("[CustomDrops]" + ChatColor.RED + " Can't find a new version!");
                sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        });
    }

    public void sendLog(String string) { getServer().getConsoleSender().sendMessage(string); }

}
