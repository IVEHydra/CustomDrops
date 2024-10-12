package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.condition.ConditionHandler;
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
    private final String number;
    private CustomDropType type;
    private ItemStack itemStack;
    private List<ItemStack> itemStacks;
    private List<String> actions;
    private int exp;

    public CustomDrop(List<String> conditions, double chance, boolean disableForNatural, boolean disableForPlaced, String number, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForPlaced = disableForPlaced;
        this.number = number;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        setEXP(exp);
    }

    public CustomDrop(List<String> conditions, double chance, boolean disableForNatural, boolean disableForSpawner, boolean disableForSpawnerEgg, String number, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForSpawner = disableForSpawner;
        this.disableForSpawnerEgg = disableForSpawnerEgg;
        this.number = number;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        setEXP(exp);
    }

    public CustomDrop(List<String> conditions, double chance, String number, CustomDropType type, ItemStack itemStack, List<ItemStack> itemStacks, List<String> actions, int exp) {
        this.conditions = conditions;
        setChance(chance);
        this.number = number;
        this.type = type;
        this.itemStack = itemStack;
        this.itemStacks = itemStacks;
        this.actions = actions;
        setEXP(exp);
    }

    public List<String> getConditions() { return conditions; }

    public void addCondition(String condition) { if(!conditions.contains(condition)) conditions.add(condition); }

    public void removeCondition(String condition) { conditions.remove(condition); }

    public boolean areConditionsTrue(Player p) {
        ConditionHandler conditionHandler = new ConditionHandler();
        return conditionHandler.handle(p, getConditions());
    }

    public double getChance() { return chance; }

    public void setChance(double value) {
        chance += value;
        chance = Math.min(Math.max(0, chance), 100);
    }

    public boolean isForNaturalDisabled() { return disableForNatural; }

    public void setForNatural(boolean disableForNatural) { this.disableForNatural = disableForNatural; }

    public boolean isForPlacedDisabled() { return disableForPlaced; }

    public void setForPlaced(boolean disableForPlaced) { this.disableForPlaced = disableForPlaced; }

    public boolean isForSpawnerDisabled() { return disableForSpawner; }

    public void setForSpawner(boolean disableForSpawner) { this.disableForSpawner = disableForSpawner; }

    public boolean isForSpawnerEggDisabled() { return disableForSpawnerEgg; }

    public void setForSpawnerEgg(boolean disableForSpawnerEgg) { this.disableForSpawnerEgg = disableForSpawnerEgg; }

    public String getNumber() { return number; }

    public CustomDropType getType() { return type; }

    public void setType(CustomDropType type) { this.type = type; }

    public ItemStack getItemStack() { return itemStack; }

    public void setItemStack(ItemStack itemStack) { this.itemStack = itemStack; }

    public List<ItemStack> getItemStacks() { return itemStacks; }

    public void addItemStacks(ItemStack itemStack) { itemStacks.add(itemStack); }

    public void removeItemStacks(ItemStack itemStack) { itemStacks.remove(itemStack); }

    public void setItemStacks(List<ItemStack> itemStacks) { this.itemStacks = itemStacks; }

    public List<String> getActions() { return actions; }

    public void addAction(String action) { if(!actions.contains(action)) actions.add(action); }

    public void removeAction(int action) { actions.remove(action); }

    public int getExp() { return exp; }

    public void setEXP(int value) {
        exp += value;
        exp = Math.min(Math.max(0, exp), 100);
    }

}
