package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.*;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomDropSelectionGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private boolean isBlock, isFishing;

    public CustomDropSelectionGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
        this.isBlock = playerGUI.isBlock();
        this.isFishing = playerGUI.isFishing();
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.customDropSelection.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropHandler customDropHandler;

        if(isBlock && !isFishing) customDropHandler = customDropManager.getBlockCustomDrops().get(playerGUI.getString());
        else if(!isBlock && !isFishing) customDropHandler = customDropManager.getEntityCustomDrops().get(playerGUI.getString());
        else customDropHandler = customDropManager.getFishingCustomDrop();

        List<CustomDrop> customDrops = customDropHandler.getCustomDrops();

        if(!customDrops.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= customDrops.size()) break;
                CustomDrop customDrop = customDrops.get(index);
                if(customDrop != null)
                    inv.addItem(createItemStack(customDrop.getNumber(), instance.getConfig().getStringList("gui.customDropSelection.items.customDrop.lore"), instance.getConfig().getString("gui.customDropSelection.items.customDrop.material")));
            }
        }

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.customDropSelection.items.add.name"), instance.getConfig().getStringList("gui.customDropSelection.items.add.lore"), instance.getConfig().getString("gui.customDropSelection.items.add.material")));

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
        CustomDropHandler customDropHandler;

        if(isBlock && !isFishing) customDropHandler = customDropManager.getBlockCustomDrops().get(playerGUI.getString());
        else if(!isBlock && !isFishing) customDropHandler = customDropManager.getEntityCustomDrops().get(playerGUI.getString());
        else customDropHandler = customDropManager.getFishingCustomDrop();

        List<CustomDrop> customDrops = customDropHandler.getCustomDrops();

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
                if(isBlock && !isFishing) new EditGUI(playerGUI, playerGUI.getString(), true, false).open();
                else if(!isBlock && !isFishing) new EditGUI(playerGUI, playerGUI.getString(), false, false).open();
                else new EditGUI(playerGUI, "", false, true).open();
                break;
            case 50:
                if(!((index + 1) >= customDrops.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(MessageUtils.LAST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                break;
            case 52:
                addCustomDrop(playerGUI.getString());
                instance.reloadCustomDropManager();
                super.open();
                break;
            default:
                String key = itemMeta.getDisplayName();
                switch(e.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        CustomDrop customDrop = customDrops.get(Integer.parseInt(ChatColor.stripColor(itemMeta.getDisplayName())));
                        new CustomDropEditGUI(playerGUI, customDrop).open();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        String path;
                        if(isBlock && !isFishing) path = "customDrops.blocks." + playerGUI.getString() + ".drops." + key;
                        else if(!isBlock && !isFishing) path = "customDrops.entities." + playerGUI.getString() + ".drops." + key;
                        else path = "customDrops.fishing.drops." + key;
                        instance.getCustomDropsFile().set(path, null);
                        instance.saveCustomDropsFile();
                        instance.reloadCustomDropManager();
                        super.open();
                        break;
                }
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) { }

    private void addCustomDrop(String name) {
        String basePath;
        if(isBlock && !isFishing) basePath = "customDrops.blocks." + name + ".drops";
        else if(!isBlock && !isFishing) basePath = "customDrops.entities." + name + ".drops";
        else basePath = "customDrops.fishing." + name + ".drops";
        int index = getIndex(name);
        String path = basePath + "." + index;
        instance.getCustomDropsFile().set(path + ".conditions", Collections.singletonList("%player_world% equalsIgnoreCase world"));
        instance.getCustomDropsFile().set(path + ".chance", 0.02);
        if(isBlock && !isFishing) {
            instance.getCustomDropsFile().set(path + ".disableForNatural", false);
            instance.getCustomDropsFile().set(path + ".disableForPlaced", false);
            instance.getCustomDropsFile().set(path + ".drop.type", "ITEM");
            instance.getCustomDropsFile().set(path + ".drop.item.==", "org.bukkit.inventory.ItemStack");
            instance.getCustomDropsFile().set(path + ".drop.item.type", "COBBLESTONE");
            instance.getCustomDropsFile().set(path + ".drop.actions", Collections.singletonList("[MESSAGE] &eTest"));
            instance.getCustomDropsFile().set(path + ".exp", 1);
        } else if(!isBlock && !isFishing) {
            instance.getCustomDropsFile().set(path + ".disableForNatural", false);
            instance.getCustomDropsFile().set(path + ".disableForSpawner", false);
            instance.getCustomDropsFile().set(path + ".disableForSpawnerEgg", false);
            instance.getCustomDropsFile().set(path + ".drop.type", "ITEM");
            instance.getCustomDropsFile().set(path + ".drop.item.==", "org.bukkit.inventory.ItemStack");
            instance.getCustomDropsFile().set(path + ".drop.item.type", "COOKED_BEEF");
            instance.getCustomDropsFile().set(path + ".drop.actions", Collections.singletonList("[MESSAGE] &eTest"));
            instance.getCustomDropsFile().set(path + ".exp", 1);
        } else {
            instance.getCustomDropsFile().set(path + ".drop.type", "ITEM");
            instance.getCustomDropsFile().set(path + ".drop.item.==", "org.bukkit.inventory.ItemStack");
            instance.getCustomDropsFile().set(path + ".drop.item.type", "APPLE");
            instance.getCustomDropsFile().set(path + ".drop.actions", Collections.singletonList("[MESSAGE] &eTest"));
            instance.getCustomDropsFile().set(path + ".exp", 1);
        }
        instance.saveCustomDropsFile();
    }

    private int getIndex(String name) {
        ConfigurationSection section;
        if(isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + name + ".drops");
        else if(!isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + name + ".drops");
        else section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops");
        if(section == null) return 0;
        int max = 0;
        for(String key : section.getKeys(false)) {
            try {
                index = Integer.parseInt(key);
                if(index >= max) max = index + 1;
            } catch(NumberFormatException e) {
                instance.sendLog("[CustomDrops]" + ChatColor.RED + " An error occurred while parsing input: " + e);
                instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        }
        return max;
    }

}
