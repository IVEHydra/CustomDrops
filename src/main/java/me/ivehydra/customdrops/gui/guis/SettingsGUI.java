package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.CustomDropSettings;
import me.ivehydra.customdrops.customdrop.multiplier.MultiplierType;
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

public class SettingsGUI extends GUI {

    private final CustomDrops instance = CustomDrops.getInstance();

    public SettingsGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.settings.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropSettings settings = customDropManager.getSettings();

        if(settings.isEnabled(MultiplierType.FORTUNE))
            inv.setItem(11, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.enabled.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.fortune"))), updateLore(instance.getConfig().getStringList("gui.settings.items.enabled.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.fortune")), instance.getConfig().getString("gui.settings.items.enabled.material.fortune")));
        else
            inv.setItem(11, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.disabled.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.fortune"))), updateLore(instance.getConfig().getStringList("gui.settings.items.disabled.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.fortune")), instance.getConfig().getString("gui.settings.items.disabled.material.fortune")));

        if(settings.isEnabled(MultiplierType.LOOTING))
            inv.setItem(13, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.enabled.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.looting"))), updateLore(instance.getConfig().getStringList("gui.settings.items.enabled.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.looting")), instance.getConfig().getString("gui.settings.items.enabled.material.looting")));
        else
            inv.setItem(13, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.disabled.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.looting"))), updateLore(instance.getConfig().getStringList("gui.settings.items.disabled.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.looting")), instance.getConfig().getString("gui.settings.items.disabled.material.looting")));

        if(settings.isEnabled(MultiplierType.LUCK))
            inv.setItem(15, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.enabled.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea"))), updateLore(instance.getConfig().getStringList("gui.settings.items.enabled.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea")), instance.getConfig().getString("gui.settings.items.enabled.material.luckOfTheSea")));
        else
            inv.setItem(15, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.disabled.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea"))), updateLore(instance.getConfig().getStringList("gui.settings.items.disabled.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea")), instance.getConfig().getString("gui.settings.items.disabled.material.luckOfTheSea")));

        inv.setItem(20, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.worlds.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.fortune"))), updateLore(instance.getConfig().getStringList("gui.settings.items.worlds.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.fortune")), instance.getConfig().getString("gui.settings.items.worlds.material")));

        inv.setItem(22, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.worlds.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.looting"))), updateLore(instance.getConfig().getStringList("gui.settings.items.worlds.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.looting")), instance.getConfig().getString("gui.settings.items.worlds.material")));

        inv.setItem(24, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.worlds.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea"))), updateLore(instance.getConfig().getStringList("gui.settings.items.worlds.lore"), "%enchantment%", instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea")), instance.getConfig().getString("gui.settings.items.worlds.material")));

        inv.setItem(29, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.percentagePerLevel.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.fortune"))), updateLore(instance.getConfig().getStringList("gui.settings.items.percentagePerLevel.lore"), "%percentagePerLevel%", String.valueOf(settings.getLevel(MultiplierType.FORTUNE))), instance.getConfig().getString("gui.settings.items.percentagePerLevel.material")));

        inv.setItem(31, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.percentagePerLevel.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.looting"))), updateLore(instance.getConfig().getStringList("gui.settings.items.percentagePerLevel.lore"), "%percentagePerLevel%", String.valueOf(settings.getLevel(MultiplierType.LOOTING))), instance.getConfig().getString("gui.settings.items.percentagePerLevel.material")));

        inv.setItem(33, createItemStack(Objects.requireNonNull(instance.getConfig().getString("gui.settings.items.percentagePerLevel.name")).replace("%enchantment%", Objects.requireNonNull(instance.getConfig().getString("gui.settings.enchantment.luckOfTheSea"))), updateLore(instance.getConfig().getStringList("gui.settings.items.percentagePerLevel.lore"), "%percentagePerLevel%", String.valueOf(settings.getLevel(MultiplierType.LUCK))), instance.getConfig().getString("gui.settings.items.percentagePerLevel.material")));

        inv.setItem(49, createItemStack(instance.getConfig().getString("gui.general.items.close.name"), instance.getConfig().getStringList("gui.general.items.close.lore"), instance.getConfig().getString("gui.general.items.close.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropSettings settings = customDropManager.getSettings();

        switch(e.getRawSlot()) {
            case 11:
                settings.setEnabled(MultiplierType.FORTUNE, !settings.isEnabled(MultiplierType.FORTUNE));
                super.open();
                break;
            case 13:
                settings.setEnabled(MultiplierType.LOOTING, !settings.isEnabled(MultiplierType.LOOTING));
                super.open();
                break;
            case 15:
                settings.setEnabled(MultiplierType.LUCK, !settings.isEnabled(MultiplierType.LUCK));
                super.open();
                break;
            case 20:
                new WorldSelectionGUI(playerGUI, "fortune", false).open();
                break;
            case 22:
                new WorldSelectionGUI(playerGUI, "looting", false).open();
                break;
            case 24:
                new WorldSelectionGUI(playerGUI, "luck", false).open();
                break;
            case 29:
                switch(e.getClick()) {
                    case LEFT:
                        settings.setLevel(MultiplierType.FORTUNE, 1);
                        break;
                    case SHIFT_LEFT:
                        settings.setLevel(MultiplierType.FORTUNE, 0.1);
                        break;
                    case RIGHT:
                        settings.setLevel(MultiplierType.FORTUNE, -1);
                        break;
                    case SHIFT_RIGHT:
                        settings.setLevel(MultiplierType.FORTUNE, -0.1);
                        break;
                }
                super.open();
                break;
            case 31:
                switch(e.getClick()) {
                    case LEFT:
                        settings.setLevel(MultiplierType.LOOTING, 1);
                        break;
                    case SHIFT_LEFT:
                        settings.setLevel(MultiplierType.LOOTING, 0.1);
                        break;
                    case RIGHT:
                        settings.setLevel(MultiplierType.LOOTING, -1);
                        break;
                    case SHIFT_RIGHT:
                        settings.setLevel(MultiplierType.LOOTING, -0.1);
                        break;
                }
                super.open();
                break;
            case 33:
                switch(e.getClick()) {
                    case LEFT:
                        settings.setLevel(MultiplierType.LUCK, 1);
                        break;
                    case SHIFT_LEFT:
                        settings.setLevel(MultiplierType.LUCK, 0.1);
                        break;
                    case RIGHT:
                        settings.setLevel(MultiplierType.LUCK, -1);
                        break;
                    case SHIFT_RIGHT:
                        settings.setLevel(MultiplierType.LUCK, -0.1);
                        break;
                }
                super.open();
                break;
            case 49:
                new MainGUI(playerGUI).open();
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropSettings settings = customDropManager.getSettings();
        ConfigurationSection fortuneSection = instance.getConfig().getConfigurationSection("customDropsSettings.fortuneMultiplier");
        ConfigurationSection lootingSection = instance.getConfig().getConfigurationSection("customDropsSettings.lootingMultiplier");
        ConfigurationSection luckSection = instance.getConfig().getConfigurationSection("customDropsSettings.luckOfTheSeaMultiplier");
        if(fortuneSection != null) {
            fortuneSection.set("enabled", settings.isEnabled(MultiplierType.FORTUNE));
            fortuneSection.set("percentagePerLevel", settings.getLevel(MultiplierType.FORTUNE));
        }
        if(lootingSection != null) {
            lootingSection.set("enabled", settings.isEnabled(MultiplierType.LOOTING));
            lootingSection.set("percentagePerLevel", settings.getLevel(MultiplierType.LOOTING));
        }
        if(luckSection != null) {
            luckSection.set("enabled", settings.isEnabled(MultiplierType.LUCK));
            luckSection.set("percentagePerLevel", settings.getLevel(MultiplierType.LUCK));
        }
        instance.saveConfig();
        instance.reloadCustomDropManager();
    }

}
