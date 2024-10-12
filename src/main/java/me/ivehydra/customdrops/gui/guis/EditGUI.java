package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDropHandler;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.gui.GUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class EditGUI extends GUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private String string;
    private boolean isBlock, isFishing;

    public EditGUI(PlayerGUI playerGUI, String string, boolean isBlock, boolean isFishing) {
        super(playerGUI);
        playerGUI.setGUI(this);
        playerGUI.setString(string);
        playerGUI.setIsBlock(isBlock);
        playerGUI.setIsFishing(isFishing);
        this.string = string;
        this.isBlock = isBlock;
        this.isFishing = isFishing;
    }

    @Override
    public String getName() {
        if(isBlock && !isFishing) return StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("gui.edit.block.title")).replace("%block_name%", string));
        else if(!isBlock && !isFishing) return StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("gui.edit.entity.title")).replace("%entity_name%", string));
        else return StringUtils.getColoredString(instance.getConfig().getString("gui.edit.fishing.title"));
    }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropHandler customDropHandler;
        String placeholder;

        if(isBlock && !isFishing) {
            customDropHandler = customDropManager.getBlockCustomDrops().get(string);
            placeholder = instance.getConfig().getString("gui.edit.placeholder.block");
        } else if(!isBlock && !isFishing) {
            customDropHandler = customDropManager.getEntityCustomDrops().get(string);
            placeholder = instance.getConfig().getString("gui.edit.placeholder.entity");
        } else {
            customDropHandler = customDropManager.getFishingCustomDrop();
            placeholder = instance.getConfig().getString("gui.edit.placeholder.fishing");
        }

        if(customDropHandler.isVanillaDropsDisabled())
            inv.setItem(11, createItemStack(instance.getConfig().getString("gui.edit.items.vanillaDrops.disabled.name"), instance.getConfig().getStringList("gui.edit.items.vanillaDrops.disabled.lore"), instance.getConfig().getString("gui.edit.items.vanillaDrops.disabled.material")));
        else
            inv.setItem(11, createItemStack(instance.getConfig().getString("gui.edit.items.vanillaDrops.enabled.name"), instance.getConfig().getStringList("gui.edit.items.vanillaDrops.enabled.lore"), instance.getConfig().getString("gui.edit.items.vanillaDrops.enabled.material")));
        if(customDropHandler.isEnabled())
            inv.setItem(13, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.edit.items.enable.enabled.name")).replace("%placeholder%", Objects.requireNonNull(placeholder)), updateLore(instance.getConfig().getStringList("gui.edit.items.enable.enabled.lore"), "%placeholder%", placeholder), instance.getConfig().getString("gui.edit.items.enable.enabled.material")));
        else
            inv.setItem(13, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.edit.items.enable.disabled.name")).replace("%placeholder%", Objects.requireNonNull(placeholder)), updateLore(instance.getConfig().getStringList("gui.edit.items.enable.disabled.lore"), "%placeholder%", placeholder), instance.getConfig().getString("gui.edit.items.enable.disabled.material")));
        if(customDropHandler.isVanillaEXPDisabled())
            inv.setItem(15, createItemStack(instance.getConfig().getString("gui.edit.items.vanillaEXP.disabled.name"), instance.getConfig().getStringList("gui.edit.items.vanillaEXP.disabled.lore"), instance.getConfig().getString("gui.edit.items.vanillaEXP.disabled.material")));
        else
            inv.setItem(15, createItemStack(instance.getConfig().getString("gui.edit.items.vanillaEXP.enabled.name"), instance.getConfig().getStringList("gui.edit.items.vanillaEXP.enabled.lore"), instance.getConfig().getString("gui.edit.items.vanillaEXP.enabled.material")));

        inv.setItem(20, createItemStack(instance.getConfig().getString("gui.edit.items.vanillaDropsWorlds.name"), instance.getConfig().getStringList("gui.edit.items.vanillaDropsWorlds.lore"), instance.getConfig().getString("gui.edit.items.vanillaDropsWorlds.material")));

        inv.setItem(22, createItemStack(instance.getConfig().getString("gui.edit.items.customDrops.name"), instance.getConfig().getStringList("gui.edit.items.customDrops.lore"), instance.getConfig().getString("gui.edit.items.customDrops.material")));

        inv.setItem(24, createItemStack(instance.getConfig().getString("gui.edit.items.vanillaEXPWorlds.name"), instance.getConfig().getStringList("gui.edit.items.vanillaEXPWorlds.lore"), instance.getConfig().getString("gui.edit.items.vanillaEXPWorlds.material")));

        inv.setItem(49, createItemStack(instance.getConfig().getString("gui.general.items.close.name"), instance.getConfig().getStringList("gui.general.items.close.lore"), instance.getConfig().getString("gui.general.items.close.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropHandler customDropHandler;
        if(isBlock && !isFishing) customDropHandler = customDropManager.getBlockCustomDrops().get(string);
        else if(!isBlock && !isFishing) customDropHandler = customDropManager.getEntityCustomDrops().get(string);
        else customDropHandler = customDropManager.getFishingCustomDrop();
        if(customDropHandler == null) return;

        switch(e.getRawSlot()) {
            case 11:
                customDropHandler.setVanillaDrops(!customDropHandler.isVanillaDropsDisabled());
                super.open();
                break;
            case 13:
                customDropHandler.setEnabled(!customDropHandler.isEnabled());
                super.open();
                break;
            case 15:
                customDropHandler.setVanillaEXP(!customDropHandler.isVanillaEXPDisabled());
                super.open();
                break;
            case 20:
                if(isBlock && !isFishing) new WorldSelectionGUI(playerGUI, "block", true).open();
                else if(!isBlock && !isFishing) new WorldSelectionGUI(playerGUI, "entity", true).open();
                else new WorldSelectionGUI(playerGUI,"fishing", true).open();
                break;
            case 22:
                new CustomDropSelectionGUI(playerGUI).open();
                break;
            case 24:
                if(isBlock && !isFishing) new WorldSelectionGUI(playerGUI, "block", false).open();
                else if(!isBlock && !isFishing) new WorldSelectionGUI(playerGUI, "entity", false).open();
                else new WorldSelectionGUI(playerGUI,"fishing", false).open();
                break;
            case 49:
                if(isBlock && !isFishing) new BlockSelectionGUI(playerGUI).open();
                else if(!isBlock && !isFishing) new EntitySelectionGUI(playerGUI).open();
                else new MainGUI(playerGUI).open();
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        ConfigurationSection section;
        CustomDropHandler customDropHandler;
        if(isBlock && !isFishing) {
            section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString().toUpperCase());
            customDropHandler = customDropManager.getBlockCustomDrops().get(playerGUI.getString());
        } else if(!isBlock && !isFishing) {
            section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString().toUpperCase());
            customDropHandler = customDropManager.getEntityCustomDrops().get(playerGUI.getString());
        } else {
            section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing");
            customDropHandler = customDropManager.getFishingCustomDrop();
        }
        if(section != null && customDropHandler != null) {
            section.set("enabled", customDropHandler.isEnabled());
            section.set("vanillaDrops.disabled", customDropHandler.isVanillaDropsDisabled());
            section.set("vanillaEXP.disabled", customDropHandler.isVanillaEXPDisabled());
        }
        instance.saveCustomDropsFile();
        instance.reloadCustomDropManager();
    }
}
