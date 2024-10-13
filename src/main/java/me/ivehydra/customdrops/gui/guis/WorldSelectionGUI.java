package me.ivehydra.customdrops.gui.guis;

import com.cryptomorin.xseries.XMaterial;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.*;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropBlock;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropEntity;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropFishing;
import me.ivehydra.customdrops.customdrop.multiplier.MultiplierType;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WorldSelectionGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private String type;
    private boolean isDrops;

    public WorldSelectionGUI(PlayerGUI playerGUI, String type, boolean isDrops) {
        super(playerGUI);
        playerGUI.setGUI(this);
        this.type = type;
        this.isDrops = isDrops;
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.worldSelection.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        List<World> worlds = instance.getServer().getWorlds();
        CustomDropManager customDropManager = instance.getCustomDropManager();

        if(!worlds.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= worlds.size()) break;
                World world = worlds.get(index);
                if(world != null) {
                    CustomDropHandler customDropHandler = null;
                    switch(type) {
                        case "block":
                            customDropHandler = customDropManager.getBlockCustomDrops().get(playerGUI.getString());
                            break;
                        case "entity":
                            customDropHandler = customDropManager.getEntityCustomDrops().get(playerGUI.getString());
                            break;
                        case "fishing":
                            customDropHandler = customDropManager.getFishingCustomDrop();
                            break;
                        default:
                            CustomDropSettings settings = customDropManager.getSettings();
                            switch(type) {
                                case "fortune":
                                    addWorld(world, settings.getWorlds(MultiplierType.FORTUNE));
                                    break;
                                case "looting":
                                    addWorld(world, settings.getWorlds(MultiplierType.LOOTING));
                                    break;
                                case "luck":
                                    addWorld(world, settings.getWorlds(MultiplierType.LUCK));
                                    break;
                            }
                            break;
                    }
                    if(customDropHandler != null) {
                        List<World> worldsDrop = isDrops ? customDropHandler.getVanillaDropsWorlds() : customDropHandler.getVanillaEXPWorlds();
                        addWorld(world, worldsDrop);
                    }
                }
            }
        }

        addItems();

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropSettings customDropSettings = customDropManager.getSettings();
        List<World> worlds = instance.getServer().getWorlds();

        switch(e.getRawSlot()) {
            case 48:
                if(page == 0) p.sendMessage(MessageUtils.FIRST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                else {
                    page = page - 1;
                    playerGUI.setPage(playerGUI.getPage() - 1);
                    super.open();
                }
                break;
            case 49:
                switch(type) {
                    case "block":
                        new EditGUI(playerGUI, playerGUI.getString(), true, false).open();
                        break;
                    case "entity":
                        new EditGUI(playerGUI, playerGUI.getString(), false, false).open();
                        break;
                    case "fishing":
                        new EditGUI(playerGUI, playerGUI.getString(), false, true).open();
                        break;
                    default:
                        new SettingsGUI(playerGUI).open();
                        break;
                }
                break;
            case 50:
                if(!((index + 1) >= worlds.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(MessageUtils.LAST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                break;
            default:
                World world = Bukkit.getWorld(ChatColor.stripColor(itemMeta.getDisplayName()));
                if(world != null) {
                    CustomDropHandler customDropHandler = null;
                    switch(type) {
                        case "block":
                            customDropHandler = customDropManager.getBlockCustomDrops().get(playerGUI.getString());
                            break;
                        case "entity":
                            customDropHandler = customDropManager.getEntityCustomDrops().get(playerGUI.getString());
                            break;
                        case "fishing":
                            customDropHandler = customDropManager.getFishingCustomDrop();
                            break;
                        case "fortune":
                            if(customDropSettings.getWorlds(MultiplierType.FORTUNE).contains(world))
                                customDropSettings.removeWorld(MultiplierType.FORTUNE, world);
                            else
                                customDropSettings.addWorld(MultiplierType.FORTUNE, world);
                            break;
                        case "looting":
                            if(customDropSettings.getWorlds(MultiplierType.LOOTING).contains(world))
                                customDropSettings.removeWorld(MultiplierType.LOOTING, world);
                            else
                                customDropSettings.addWorld(MultiplierType.LOOTING, world);
                            break;
                        case "luck":
                            if(customDropSettings.getWorlds(MultiplierType.LUCK).contains(world))
                                customDropSettings.removeWorld(MultiplierType.LUCK, world);
                            else
                                customDropSettings.addWorld(MultiplierType.LUCK, world);
                            break;
                    }
                    if(customDropHandler != null) {
                        List<World> worldsDrop = isDrops ? customDropHandler.getVanillaDropsWorlds() : customDropHandler.getVanillaEXPWorlds();
                        if(worldsDrop.contains(world)) {
                            if(isDrops) customDropHandler.removeVanillaDropsWorld(world);
                            else customDropHandler.removeVanillaEXPWorld(world);
                        } else {
                            if(isDrops) customDropHandler.addVanillaDropsWorld(world);
                            else customDropHandler.addVanillaEXPWorld(world);
                        }
                    }
                    super.open();
                }
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropSettings settings = customDropManager.getSettings();
        ConfigurationSection section;
        switch(type) {
            case "block":
                section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString().toUpperCase());
                if(section != null) {
                    CustomDropBlock customDropBlock = customDropManager.getBlockCustomDrops().get(playerGUI.getString());
                    section.set("vanillaDrops.worlds", customDropBlock.getVanillaDropsWorlds().stream().map(World::getName).collect(Collectors.toList()));
                    section.set("vanillaEXP.worlds", customDropBlock.getVanillaEXPWorlds().stream().map(World::getName).collect(Collectors.toList()));
                }
                break;
            case "entity":
                section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString().toUpperCase());
                if(section != null) {
                    CustomDropEntity customDropEntity = customDropManager.getEntityCustomDrops().get(playerGUI.getString());
                    section.set("vanillaDrops.worlds", customDropEntity.getVanillaDropsWorlds().stream().map(World::getName).collect(Collectors.toList()));
                    section.set("vanillaEXP.worlds", customDropEntity.getVanillaEXPWorlds().stream().map(World::getName).collect(Collectors.toList()));
                }
                break;
            case "fishing":
                section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing");
                if(section != null) {
                    CustomDropFishing customDropFishing = customDropManager.getFishingCustomDrop();
                    section.set("vanillaDrops.worlds", customDropFishing.getVanillaDropsWorlds().stream().map(World::getName).collect(Collectors.toList()));
                    section.set("vanillaEXP.worlds", customDropFishing.getVanillaEXPWorlds().stream().map(World::getName).collect(Collectors.toList()));
                }
                break;
            case "fortune":
                section = instance.getConfig().getConfigurationSection("customDropsSettings.fortuneMultiplier");
                if(section != null)
                    section.set("worlds", settings.getWorlds(MultiplierType.FORTUNE).stream().map(World::getName).collect(Collectors.toList()));
                break;
            case "looting":
                section = instance.getConfig().getConfigurationSection("customDropsSettings.lootingMultiplier");
                if(section != null)
                    section.set("worlds", settings.getWorlds(MultiplierType.LOOTING).stream().map(World::getName).collect(Collectors.toList()));
                break;
            case "luck":
                section = instance.getConfig().getConfigurationSection("customDropsSettings.luckOfTheSeaMultiplier");
                if(section != null)
                    section.set("worlds", settings.getWorlds(MultiplierType.LUCK).stream().map(World::getName).collect(Collectors.toList()));
                break;
        }
        instance.saveConfig();
        instance.saveCustomDropsFile();
        instance.reload();
    }

    private String getMaterial(World world) {
        switch(world.getEnvironment()) {
            case NETHER:
                return Objects.requireNonNull(XMaterial.NETHERRACK.parseMaterial()).name();
            case THE_END:
                return Objects.requireNonNull(XMaterial.END_STONE.parseMaterial()).name();
            case NORMAL:
            default:
                return Objects.requireNonNull(XMaterial.GRASS_BLOCK.parseMaterial()).name();
        }
    }

    private void addWorld(World world, List<World> worlds) {
        String name = worlds.contains(world) ? instance.getConfig().getString("gui.worldSelection.items.world.contains.name") : instance.getConfig().getString("gui.worldSelection.items.world.notContains.name");
        List<String> lore = worlds.contains(world) ? instance.getConfig().getStringList("gui.worldSelection.items.world.contains.lore") : instance.getConfig().getStringList("gui.worldSelection.items.world.notContains.lore");
        inv.addItem(createItemStack(Objects.requireNonNull(name).replace("%world_name%", world.getName()), lore, getMaterial(world)));
    }

}
