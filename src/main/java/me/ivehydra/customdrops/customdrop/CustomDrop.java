package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.condition.ConditionHandler;
import me.ivehydra.customdrops.customdrop.multiplier.Multiplier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomDrop {

    private List<String> conditions;
    private double chance;
    private boolean disableForNatural;
    private boolean disableForPlaced;
    private boolean disableForSpawner;
    private boolean disableForSpawnerEgg;
    private Multiplier chanceMultiplier;
    private boolean autoPickup;
    private CustomDropType type;
    private ItemStack itemStack;
    private List<ItemStack> itemStacks;
    private List<String> actions;
    private List<CustomDropEXP> customDropEXPs;

    public CustomDrop(List<String> conditions, double chance, boolean disableForNatural, boolean disableForPlaced, Multiplier chanceMultiplier, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, List<CustomDropEXP> customDropEXPs) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForPlaced = disableForPlaced;
        this.chanceMultiplier = chanceMultiplier;
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.customDropEXPs = customDropEXPs;
    }

    public CustomDrop(List<String> conditions, double chance, boolean disableForNatural, boolean disableForSpawner, boolean disableForSpawnerEgg, Multiplier chanceMultiplier, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, List<CustomDropEXP> customDropEXPs) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForSpawner = disableForSpawner;
        this.disableForSpawnerEgg = disableForSpawnerEgg;
        this.chanceMultiplier = chanceMultiplier;
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.customDropEXPs = customDropEXPs;
    }

    public CustomDrop(List<String> conditions, double chance, Multiplier chanceMultiplier, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, List<CustomDropEXP> customDropEXPs) {
        this.conditions = conditions;
        setChance(chance);
        this.chanceMultiplier = chanceMultiplier;
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.customDropEXPs = customDropEXPs;
    }

    public boolean areConditionsTrue(Player p) {
        ConditionHandler conditionHandler = new ConditionHandler();
        return conditionHandler.handle(p, conditions);
    }

    public double getChance() { return chance; }

    private void setChance(double value) {
        chance += value;
        chance = Math.min(Math.max(0, chance), 100);
    }

    public boolean isForNaturalDisabled() { return disableForNatural; }

    public boolean isForPlacedDisabled() { return disableForPlaced; }

    public boolean isForSpawnerDisabled() { return disableForSpawner; }

    public boolean isForSpawnerEggDisabled() { return disableForSpawnerEgg; }

    public Multiplier getChanceMultiplier() { return chanceMultiplier; }

    public boolean isAutoPickupEnabled() { return autoPickup; }

    public CustomDropType getType() { return type; }

    public ItemStack getItemStack() { return itemStack; }

    public void setItemStack(ItemStack itemStack) { this.itemStack = itemStack; }

    public List<ItemStack> getItemStacks() { return itemStacks; }

    public void addItemStacks(ItemStack itemStack) { itemStacks.add(itemStack); }

    public void removeItemStacks(ItemStack itemStack) { itemStacks.remove(itemStack); }

    public List<String> getActions() { return actions; }

    public List<CustomDropEXP> getCustomDropEXPs() { return customDropEXPs; }

}
