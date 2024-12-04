package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.gui.GUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class ItemSelectionGUI extends GUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private String type;

    public ItemSelectionGUI(PlayerGUI playerGUI, String type) {
        super(playerGUI);
        playerGUI.setGUI(this);
        this.type = type;
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.itemSelection.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        Player p = playerGUI.getPlayer();
        PlayerInventory pInv = p.getInventory();
        ItemStack[] itemStacks = pInv.getContents();

        for(ItemStack itemStack : itemStacks) {
            if(itemStack != null && itemStack.getType() != Material.AIR)
                inv.addItem(itemStack);
        }

        inv.setItem(49, createItemStack(instance.getConfig().getString("gui.general.items.close.name"), instance.getConfig().getStringList("gui.general.items.close.lore"), instance.getConfig().getString("gui.general.items.close.material")));
    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        CustomDrop customDrop = playerGUI.getCustomDrop();

        if(e.getRawSlot() == 49)
            new CustomDropEditingGUI(playerGUI, playerGUI.getString(), type, customDrop).open();
        else {
            switch(customDrop.getType()) {
                case ITEM:
                    customDrop.setItemStack(itemStack);
                    break;
                case ITEMS:
                    customDrop.addItemStacks(itemStack);
                    break;
            }
            new CustomDropEditingGUI(playerGUI, playerGUI.getString(), type, customDrop).open();
        }

    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {}

}
