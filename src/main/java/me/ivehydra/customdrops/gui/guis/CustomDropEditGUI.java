package me.ivehydra.customdrops.gui.guis;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropType;
import me.ivehydra.customdrops.gui.GUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomDropEditGUI extends GUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private CustomDrop customDrop;
    private boolean isBlock, isFishing;

    public CustomDropEditGUI(PlayerGUI playerGUI, CustomDrop customDrop) {
        super(playerGUI);
        playerGUI.setGUI(this);
        playerGUI.setCustomDrop(customDrop);
        this.customDrop = customDrop;
        this.isBlock = playerGUI.isBlock();
        this.isFishing = playerGUI.isFishing();
    }

    @Override
    public String getName() { return StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("gui.customDropEdit.title")).replace("%customDrop_name%", customDrop.getNumber())); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {

        inv.setItem(10, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.conditions.name"), instance.getConfig().getStringList("gui.customDropEdit.items.conditions.lore"), instance.getConfig().getString("gui.customDropEdit.items.conditions.material")));

        inv.setItem(12, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.chance.name"), updateLore(instance.getConfig().getStringList("gui.customDropEdit.items.chance.lore"), "%customDrop_chance%", String.valueOf(customDrop.getChance())), instance.getConfig().getString("gui.customDropEdit.items.chance.material")));

        if(isBlock && !isFishing) {
            if(customDrop.isForNaturalDisabled())
                inv.setItem(14, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forNatural.block.disabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forNatural.block.disabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forNatural.block.disabled.material")));
            else
                inv.setItem(14, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forNatural.block.enabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forNatural.block.enabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forNatural.block.enabled.material")));
            if(customDrop.isForPlacedDisabled())
                inv.setItem(16, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forPlaced.disabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forPlaced.disabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forPlaced.disabled.material")));
            else
                inv.setItem(16, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forPlaced.enabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forPlaced.enabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forPlaced.enabled.material")));
        } else if(!isBlock && !isFishing) {
            if(customDrop.isForNaturalDisabled())
                inv.setItem(14, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forNatural.entity.disabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forNatural.entity.disabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forNatural.entity.disabled.material")));
            else
                inv.setItem(14, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forNatural.entity.enabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forNatural.entity.enabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forNatural.entity.enabled.material")));
            if(customDrop.isForSpawnerDisabled())
                inv.setItem(16, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forSpawner.disabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forSpawner.disabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forSpawner.disabled.material")));
            else
                inv.setItem(16, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forSpawner.enabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forSpawner.enabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forSpawner.enabled.material")));
            if(customDrop.isForSpawnerEggDisabled())
                inv.setItem(17, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forSpawnerEgg.disabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forSpawnerEgg.disabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forSpawnerEgg.disabled.material")));
            else
                inv.setItem(17, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.forSpawnerEgg.enabled.name"), instance.getConfig().getStringList("gui.customDropEdit.items.forSpawnerEgg.enabled.lore"), instance.getConfig().getString("gui.customDropEdit.items.forSpawnerEgg.enabled.material")));
        }

        inv.setItem(28, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.drop.type.name"), updateLore(instance.getConfig().getStringList("gui.customDropEdit.items.drop.type.lore"), "%customDrop_type%", customDrop.getType().name()), instance.getConfig().getString("gui.customDropEdit.items.drop.type.material")));

        inv.setItem(30, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.drop.item.name"), instance.getConfig().getStringList("gui.customDropEdit.items.drop.item.lore"), instance.getConfig().getString("gui.customDropEdit.items.drop.item.material")));

        inv.setItem(32, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.drop.actions.name"), instance.getConfig().getStringList("gui.customDropEdit.items.drop.actions.lore"), instance.getConfig().getString("gui.customDropEdit.items.drop.actions.material")));

        inv.setItem(34, createItemStack(instance.getConfig().getString("gui.customDropEdit.items.exp.name"), updateLore(instance.getConfig().getStringList("gui.customDropEdit.items.exp.lore"), "%customDrop_exp%", String.valueOf(customDrop.getExp())), instance.getConfig().getString("gui.customDropEdit.items.exp.material")));

        inv.setItem(49, createItemStack(instance.getConfig().getString("gui.general.items.close.name"), instance.getConfig().getStringList("gui.general.items.close.lore"), instance.getConfig().getString("gui.general.items.close.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        switch(e.getRawSlot()) {
            case 10:
                new ConditionsEditGUI(playerGUI).open();
                break;
            case 12:
                switch(e.getClick()) {
                    case LEFT:
                        customDrop.setChance(1);
                        break;
                    case SHIFT_LEFT:
                        customDrop.setChance(0.1);
                        break;
                    case RIGHT:
                        customDrop.setChance(-1);
                        break;
                    case SHIFT_RIGHT:
                        customDrop.setChance(-0.1);
                        break;
                }
                super.open();
                break;
            case 14:
                customDrop.setForNatural(!customDrop.isForNaturalDisabled());
                super.open();
                break;
            case 16:
                if(isBlock && !isFishing) customDrop.setForPlaced(!customDrop.isForPlacedDisabled());
                else if(!isBlock && !isFishing) customDrop.setForSpawner(!customDrop.isForSpawnerDisabled());
                super.open();
                break;
            case 17:
                if(!isBlock && !isFishing) {
                    customDrop.setForSpawnerEgg(!customDrop.isForSpawnerEggDisabled());
                    super.open();
                }
                break;
            case 28:
                ConfigurationSection section;
                if(isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString() + ".drops." + customDrop.getNumber() + ".drop");
                else if(!isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString() + ".drops." + customDrop.getNumber() + ".drop");
                else section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops." + customDrop.getNumber() + ".drop");
                if(section == null) return;
                switch(customDrop.getType()) {
                    case ITEM:
                        customDrop.setType(CustomDropType.ITEMS);
                        itemStack = customDrop.getItemStack();
                        customDrop.setItemStack(new ItemStack(Material.AIR));
                        customDrop.addItemStacks(itemStack);
                        break;
                    case ITEMS:
                        customDrop.setType(CustomDropType.ITEM);
                        List<ItemStack> itemStacks = customDrop.getItemStacks();
                        if(itemStacks.isEmpty()) itemStack = new ItemStack(Material.AIR);
                        else itemStack = itemStacks.get(0);
                        customDrop.setItemStack(itemStack);
                        customDrop.setItemStacks(new ArrayList<>());
                        break;
                }
                instance.saveCustomDropsFile();
                instance.reloadCustomDropManager();
                super.open();
                break;
            case 30:
                new ItemsEditGUI(playerGUI).open();
                break;
            case 32:
                new ActionsEditGUI(playerGUI).open();
                break;
            case 34:
                switch(e.getClick()) {
                    case LEFT:
                        customDrop.setEXP(1);
                        break;
                    case RIGHT:
                        customDrop.setEXP(-1);
                        break;
                }
                super.open();
                break;
            case 49:
                new CustomDropSelectionGUI(playerGUI).open();
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        ConfigurationSection section;
        if(isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString() + ".drops." + customDrop.getNumber());
        else if(!isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString() + ".drops." + customDrop.getNumber());
        else section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops." + customDrop.getNumber());

        if(section != null) {
            section.set("chance", customDrop.getChance());
            if(isBlock && !isFishing) {
                section.set("disableForNatural", customDrop.isForNaturalDisabled());
                section.set("disableForPlaced", customDrop.isForPlacedDisabled());
            } else if(!isBlock && !isFishing) {
                section.set("disableForNatural", customDrop.isForNaturalDisabled());
                section.set("disableForSpawner", customDrop.isForSpawnerDisabled());
                section.set("disableForSpawnerEgg", customDrop.isForSpawnerEggDisabled());
            }
            ConfigurationSection dropSection = section.getConfigurationSection("drop");
            if(dropSection != null) {
                switch(customDrop.getType()) {
                    case ITEM:
                        dropSection.set("type", "ITEM");
                        dropSection.set("items", null);
                        dropSection.set("item", customDrop.getItemStack());
                        break;
                    case ITEMS:
                        dropSection.set("type", "ITEMS");
                        dropSection.set("item", null);
                        List<ItemStack> itemStacks = customDrop.getItemStacks();
                        for(int i = 0; i < itemStacks.size(); i++) {
                            ItemStack itemStack = itemStacks.get(i);
                            if(itemStack != null)
                                dropSection.set("items." + i, itemStack);
                        }
                        break;
                }
            }
            section.set("exp", customDrop.getExp());
        }
        instance.saveCustomDropsFile();
        instance.reloadCustomDropManager();
    }
}
