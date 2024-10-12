package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.gui.GUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MainGUI extends GUI {

    private final CustomDrops instance = CustomDrops.getInstance();

    public MainGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.main.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {

        inv.setItem(13, createItemStack(instance.getConfig().getString("gui.main.items.block.name"), instance.getConfig().getStringList("gui.main.items.block.lore"), instance.getConfig().getString("gui.main.items.block.material")));

        inv.setItem(21, createItemStack(instance.getConfig().getString("gui.main.items.entity.name"), instance.getConfig().getStringList("gui.main.items.entity.lore"), instance.getConfig().getString("gui.main.items.entity.material")));

        inv.setItem(23, createItemStack(instance.getConfig().getString("gui.main.items.fishing.name"), instance.getConfig().getStringList("gui.main.items.fishing.lore"), instance.getConfig().getString("gui.main.items.fishing.material")));

        inv.setItem(31, createItemStack(instance.getConfig().getString("gui.main.items.settings.name"), instance.getConfig().getStringList("gui.main.items.settings.lore"), instance.getConfig().getString("gui.main.items.settings.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        switch(e.getRawSlot()) {
            case 13:
                new BlockSelectionGUI(playerGUI).open();
                break;
            case 21:
                new EntitySelectionGUI(playerGUI).open();
                break;
            case 23:
                new EditGUI(playerGUI, "", false, true).open();
                break;
            case 31:
                new SettingsGUI(playerGUI).open();
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) { }

}
