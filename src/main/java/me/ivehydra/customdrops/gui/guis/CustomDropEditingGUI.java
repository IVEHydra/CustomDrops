package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.file.FileManager;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class CustomDropEditingGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private String string;
    private String type;
    private CustomDrop customDrop;

    public CustomDropEditingGUI(PlayerGUI playerGUI, String string, String type, CustomDrop customDrop) {
        super(playerGUI);
        playerGUI.setGUI(this);
        playerGUI.setString(string);
        playerGUI.setCustomDrop(customDrop);
        this.string = string;
        this.type = type;
        this.customDrop = customDrop;
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.customDropEditing.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        ItemStack itemStack;
        List<ItemStack> itemStacks;

        if(customDrop == null) return;
        switch(customDrop.getType()) {
            case ITEM:
                itemStack = customDrop.getItemStack();
                if(itemStack != null)
                    inv.setItem(22, customDrop.getItemStack());
                break;
            case ITEMS:
                itemStacks = customDrop.getItemStacks();
                if(!itemStacks.isEmpty())
                    for(int i = 0; i < getMaxItemsPerPage(); i++) {
                        index = getMaxItemsPerPage() * page + i;
                        if(index >= itemStacks.size()) break;
                        itemStack = itemStacks.get(index);
                        if(itemStack != null)
                            inv.setItem(inv.firstEmpty(), itemStack);
                    }
                break;
        }

        inv.setItem(46, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.customDropEditing.items.type.name")).replace("%customdrop_type%", customDrop.getType().toString()), instance.getConfig().getStringList("gui.customDropEditing.items.type.lore"), instance.getConfig().getString("gui.customDropEditing.items.type.material")));

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.customDropEditing.items.add.name"), instance.getConfig().getStringList("gui.customDropEditing.items.add.lore"), instance.getConfig().getString("gui.customDropEditing.items.add.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        ItemStack customDropItemStack;
        List<ItemStack> itemStacks;
        switch(e.getRawSlot()) {
            case 46:
                customDrop.setType();
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(instance, super::open, 2L);
                break;
            case 48:
                if(page == 0) p.sendMessage(MessageUtils.FIRST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                else {
                    page = page - 1;
                    playerGUI.setPage(playerGUI.getPage() - 1);
                    super.open();
                }
                break;
            case 49:
                p.closeInventory();
                break;
            case 50:
                itemStacks = customDrop.getItemStacks();
                if(!((index + 1) >= itemStacks.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else
                    p.sendMessage(MessageUtils.LAST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                break;
            case 52:
                switch(customDrop.getType()) {
                    case ITEM:
                        customDropItemStack = customDrop.getItemStack();
                        if(customDropItemStack == null || customDropItemStack.getType() == Material.AIR)
                            new ItemSelectionGUI(playerGUI, type).open();
                        else
                            p.sendMessage(MessageUtils.GUI_ALREADY_EXIST.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        break;
                    case ITEMS:
                        new ItemSelectionGUI(playerGUI, type).open();
                        break;
                }
                break;
            default:
                switch(customDrop.getType()) {
                    case ITEM:
                        customDrop.setItemStack(null);
                        break;
                    case ITEMS:
                        customDrop.removeItemStacks(itemStack);
                        break;
                }
                super.open();
                break;
        }

    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        FileManager fileManager = instance.getFileManager();
        ConfigurationSection section = getConfigurationSection(fileManager);
        if(section == null) return;
        ConfigurationSection customDropsSection = section.getConfigurationSection("drops");
        if(customDropsSection != null) {
            String number = customDrop.getNumber();
            ConfigurationSection customDropSection = customDropsSection.getConfigurationSection(number + ".drop");
            if(customDropSection != null) {
                customDropSection.set("type", customDrop.getType().toString());
                ItemStack itemStack;
                ConfigurationSection itemsSection;
                switch(customDrop.getType()) {
                    case ITEM:
                        if(customDropSection.contains("items")) {
                            itemsSection = customDropSection.getConfigurationSection("items");
                            if(itemsSection != null) {
                                if(!itemsSection.getKeys(false).isEmpty()) {
                                    itemStack = customDrop.getItemStacks().get(0);
                                    customDrop.setItemStack(itemStack);
                                    customDrop.getItemStacks().clear();
                                } else {
                                    itemStack = new ItemStack(Material.AIR);
                                    customDrop.setItemStack(itemStack);
                                }
                                customDropSection.set("items", null);
                                customDropSection.set("item", itemStack);
                            }
                        } else {
                            itemStack = customDrop.getItemStack();
                            if(itemStack == null || itemStack.getType() == Material.AIR)
                                customDropSection.set("item", new ItemStack(Material.AIR));
                            else
                                customDropSection.set("item", itemStack);
                        }
                        break;
                    case ITEMS:
                        if(customDropSection.contains("item")) {
                            itemStack = customDrop.getItemStack();
                            customDrop.addItemStacks(itemStack);
                            customDrop.setItemStack(null);
                            customDropSection.set("item", null);
                            itemsSection = customDropSection.createSection("items");
                            itemsSection.set("0", itemStack);
                        } else
                            itemsSection = customDropSection.getConfigurationSection("items");
                        if(itemsSection != null) {
                            itemsSection.getKeys(false).forEach(key -> itemsSection.set(key, null));
                            List<ItemStack> itemStacks = customDrop.getItemStacks();
                            if(itemStacks != null && !itemStacks.isEmpty()) {
                                if(itemStacks.size() > 1)
                                    itemStacks.removeIf(material -> material != null && material.getType() == Material.AIR);
                                for(int i = 0; i < itemStacks.size(); i++)
                                    itemsSection.set(String.valueOf(i), itemStacks.get(i));
                            }
                        }
                        break;
                }
                fileManager.saveAll();
                instance.reloadCustomDropManager();
            }
        }

        Player p = (Player) e.getPlayer();
        instance.removePlayerGUI(p);

    }

    private ConfigurationSection getConfigurationSection(FileManager fileManager) {
        YamlConfiguration config;
        ConfigurationSection section;
        if(string == null || string.isEmpty()) {
            if(type.equalsIgnoreCase("fishing"))
                config = fileManager.getFile("drops", "fishing.yml").getConfig();
            else
                config = fileManager.getFile("drops", "piglinbartering.yml").getConfig();
            section = config.getConfigurationSection("customDrops." + type);
        } else {
            if(type.equalsIgnoreCase("blocks"))
                config = fileManager.getFile("drops", "blocks.yml").getConfig();
            else
                config = fileManager.getFile("drops", "entities.yml").getConfig();
            section = config.getConfigurationSection("customDrops." + type + "." + string);
        }
        return section;
    }
}
