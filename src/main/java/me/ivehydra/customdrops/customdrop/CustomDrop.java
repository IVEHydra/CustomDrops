package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.condition.ConditionHandler;
import me.ivehydra.customdrops.customdrop.multiplier.Multiplier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomDrop {

    private String number;
    private List<String> conditions;
    private double chance;
    private boolean disableForNatural;
    private boolean disableForPlaced;
    private boolean disableForSpawner;
    private boolean disableForSpawnerEgg;
    private Multiplier multiplier;
    private boolean autoPickup;
    private CustomDropType type;
    private ItemStack itemStack;
    private List<ItemStack> itemStacks;
    private List<String> actions;
    private int exp;

    public CustomDrop(String number, List<String> conditions, double chance, boolean disableForNatural, boolean disableForPlaced, Multiplier multiplier, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.number = number;
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForPlaced = disableForPlaced;
        this.multiplier = multiplier;
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.exp = exp;
    }

    public CustomDrop(String number, List<String> conditions, double chance, boolean disableForNatural, boolean disableForSpawner, boolean disableForSpawnerEgg, Multiplier multiplier, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.number = number;
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForSpawner = disableForSpawner;
        this.disableForSpawnerEgg = disableForSpawnerEgg;
        this.multiplier = multiplier;
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.exp = exp;
    }

    public CustomDrop(String number, List<String> conditions, double chance, Multiplier multiplier, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.number = number;
        this.conditions = conditions;
        setChance(chance);
        this.multiplier = multiplier;
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.exp = exp;
    }

    public CustomDrop(String number, List<String> conditions, double chance, boolean autoPickup, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.number = number;
        this.conditions = conditions;
        setChance(chance);
        this.autoPickup = autoPickup;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        this.exp = exp;
    }

    public String getNumber() { return number; }

    public boolean areConditionsTrue(Player p) {
        ConditionHandler conditionHandler = new ConditionHandler();
        return conditionHandler.handle(p, conditions);
    }

    public double getChance() { return chance; }

    private void setChance(double value) { this.chance = Math.min(Math.max(0, value), 100); }

    public boolean isForNaturalDisabled() { return disableForNatural; }

    public boolean isForPlacedDisabled() { return disableForPlaced; }

    public boolean isForSpawnerDisabled() { return disableForSpawner; }

    public boolean isForSpawnerEggDisabled() { return disableForSpawnerEgg; }

    public Multiplier getChanceMultiplier() { return multiplier; }

    public boolean isAutoPickupEnabled() { return autoPickup; }

    public CustomDropType getType() { return type; }

    public void setType() { this.type = (this.type == CustomDropType.ITEM) ? CustomDropType.ITEMS : CustomDropType.ITEM; }

    public ItemStack getItemStack() { return itemStack; }

    public void setItemStack(ItemStack itemStack) { this.itemStack = itemStack; }

    public List<ItemStack> getItemStacks() { return itemStacks; }

    public void addItemStacks(ItemStack itemStack) { itemStacks.add(itemStack); }

    public void removeItemStacks(ItemStack itemStack) { itemStacks.remove(itemStack); }

    public List<String> getActions() { return actions; }

    public int getEXP() { return exp; }

}
