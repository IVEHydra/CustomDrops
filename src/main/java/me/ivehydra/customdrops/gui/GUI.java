package me.ivehydra.customdrops.gui;

import me.ivehydra.customdrops.utils.MaterialUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
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

    public ItemStack createItemStack(String name, List<String> lore, String material) {
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(MaterialUtils.parse(material)));
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return null;
        itemMeta.setDisplayName(StringUtils.getColoredString(name));
        lore = lore.stream().map(StringUtils::getColoredString).collect(Collectors.toList());
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public List<String> updateLore(List<String> lore, String placeholder, String value) { return lore.stream().map(string -> string.replace(placeholder, value)).collect(Collectors.toList()); }

}
