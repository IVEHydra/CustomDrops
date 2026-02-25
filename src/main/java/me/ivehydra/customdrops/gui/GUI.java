package me.ivehydra.customdrops.gui;

import me.ivehydra.customdrops.utils.MaterialUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GUI implements InventoryHolder {

    protected Inventory inv;
    protected PlayerGUI playerGUI;

    public GUI(PlayerGUI playerGUI) { this.playerGUI = playerGUI; }

    @Override
    public @NotNull Inventory getInventory() { return inv; }

    public abstract String getName();

    public abstract int getSlots();

    public abstract void setItems();

    public void open() {
        inv = Bukkit.createInventory(this, getSlots(), getName());

        this.setItems();

        playerGUI.getPlayer().openInventory(inv);
    }

    public abstract void handleInventoryClickEvent(InventoryClickEvent e);

    public abstract void handleInventoryCloseEvent(InventoryCloseEvent e);

    public ItemStack createItemStack(String name, List<String> lore, String materialName) {
        Material material = MaterialUtils.parse(materialName);
        if(material == null)
            throw new IllegalArgumentException("[CustomDrops]" + ChatColor.RED + " Material not found: " + materialName);
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null)
            throw new IllegalStateException("[CustomDrops]" + ChatColor.RED + " ItemMeta is null!");
        itemMeta.setDisplayName(StringUtils.getColoredString(name));
        itemMeta.setLore(lore.stream().map(StringUtils::getColoredString).collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
