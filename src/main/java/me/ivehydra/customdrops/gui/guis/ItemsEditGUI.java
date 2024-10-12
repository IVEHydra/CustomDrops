package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class ItemsEditGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private boolean isBlock, isFishing;

    public ItemsEditGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
        this.isBlock = playerGUI.isBlock();
        this.isFishing = playerGUI.isFishing();
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.itemsEdit.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDrop customDrop = playerGUI.getCustomDrop();
        ItemStack itemStack;

        switch(customDrop.getType()) {
            case ITEM:
                itemStack = customDrop.getItemStack();
                if(itemStack != null)
                    inv.setItem(22, itemStack);
                break;
            case ITEMS:
                List<ItemStack> itemStacks = customDrop.getItemStacks();
                if(!itemStacks.isEmpty()) {
                    for(int i = 0; i < getMaxItemsPerPage(); i++) {
                        index = getMaxItemsPerPage() * page + i;
                        if(index >= itemStacks.size()) break;
                        itemStack = itemStacks.get(index);
                        if(itemStack != null)
                            inv.addItem(itemStack);
                    }
                }
                break;
        }

        inv.setItem(46, createItemStack(instance.getConfig().getString("gui.itemsEdit.items.information.name"), instance.getConfig().getStringList("gui.itemsEdit.items.information.lore"), instance.getConfig().getString("gui.itemsEdit.items.information.material")));

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.itemsEdit.items.add.name"), instance.getConfig().getStringList("gui.itemsEdit.items.add.lore"), instance.getConfig().getString("gui.itemsEdit.items.add.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        CustomDrop customDrop = playerGUI.getCustomDrop();

        switch(e.getRawSlot()) {
            case 46:
                break;
            case 48:
                if(page == 0) p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.gui.firstPage")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
                else {
                    page = page - 1;
                    playerGUI.setPage(playerGUI.getPage() - 1);
                    super.open();
                }
                break;
            case 49:
                new CustomDropEditGUI(playerGUI, playerGUI.getCustomDrop()).open();
                break;
            case 50:
                List<ItemStack> itemStacks = customDrop.getItemStacks();
                if(!((index + 1) >= itemStacks.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.gui.lastPage")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
                break;
            case 52:
                switch(customDrop.getType()) {
                    case ITEM:
                        itemStack = customDrop.getItemStack();
                        if(itemStack == null || itemStack.getType() == Material.AIR) {
                            new ItemSelectionGUI(playerGUI).open();
                            return;
                        }
                        p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.gui.alreadyExist")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
                        break;
                    case ITEMS:
                        new ItemSelectionGUI(playerGUI).open();
                        break;
                }
                break;
            default:
                switch(customDrop.getType()) {
                    case ITEM:
                        customDrop.setItemStack(new ItemStack(Material.AIR));
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
        CustomDrop customDrop = playerGUI.getCustomDrop();
        ConfigurationSection section;
        if(isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString() + ".drops." + customDrop.getNumber() + ".drop");
        else if(!isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString() + ".drops." + customDrop.getNumber() + ".drop");
        else section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops." + customDrop.getNumber() + ".drop");
        if(section != null) {
            ItemStack itemStack;
            switch(customDrop.getType()) {
                case ITEM:
                    itemStack = customDrop.getItemStack();
                    if(itemStack == null || itemStack.getType() == Material.AIR)
                        itemStack = new ItemStack(Material.AIR);
                    section.set("item", itemStack);
                    break;
                case ITEMS:
                    ConfigurationSection itemsSection = section.getConfigurationSection("items");
                    if(itemsSection != null) {
                        itemsSection.getKeys(false).forEach(key -> itemsSection.set(key, null));
                        List<ItemStack> itemStacks = customDrop.getItemStacks();
                        if(!itemStacks.isEmpty()) {
                            for(int i = 0;  i < itemStacks.size(); i++) {
                                itemStack = itemStacks.get(i);
                                if(itemStack != null)
                                    itemsSection.set(String.valueOf(i), itemStack);
                            }
                        }
                    }
                    break;
            }
            instance.saveCustomDropsFile();
            instance.reloadCustomDropManager();
        }
    }
}
