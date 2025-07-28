package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.condition.ConditionHandler;
import me.ivehydra.customdrops.customdrop.multiplier.ChanceMultiplier;
import org.bukkit.entity.Player;

import java.util.List;

public class CustomDropEXP {

    private List<String> conditions;
    private double chance;
    private boolean disableForNatural;
    private boolean disableForPlaced;
    private boolean disableForSpawner;
    private boolean disableForSpawnerEgg;
    private ChanceMultiplier chanceMultiplier;
    private ChanceMultiplier expChanceMultiplier;
    private int exp;
    private List<String> actions;

    public CustomDropEXP(List<String> conditions, double chance, boolean disableForNatural, boolean disableForPlaced, ChanceMultiplier chanceMultiplier, ChanceMultiplier expChanceMultiplier, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForPlaced = disableForPlaced;
        this.chanceMultiplier = chanceMultiplier;
        this.expChanceMultiplier = expChanceMultiplier;
        setEXP(exp);
        this.actions = actions;
    }

    public CustomDropEXP(List<String> conditions, double chance, boolean disableForNatural, boolean disableForSpawner, boolean disableForSpawnerEgg, ChanceMultiplier chanceMultiplier, ChanceMultiplier expChanceMultiplier, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForSpawner = disableForSpawner;
        this.disableForSpawnerEgg = disableForSpawnerEgg;
        this.chanceMultiplier = chanceMultiplier;
        this.expChanceMultiplier = expChanceMultiplier;
        setEXP(exp);
        this.actions = actions;
    }

    public CustomDropEXP(List<String> conditions, double chance, ChanceMultiplier chanceMultiplier, ChanceMultiplier expChanceMultiplier, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        this.chanceMultiplier = chanceMultiplier;
        this.expChanceMultiplier = expChanceMultiplier;
        setEXP(exp);
        this.actions = actions;
    }

    public CustomDropEXP(List<String> conditions, double chance, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        setEXP(exp);
        this.actions = actions;
    }

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

    public ChanceMultiplier getChanceMultiplier() { return chanceMultiplier; }

    public ChanceMultiplier getEXPMultiplier() { return expChanceMultiplier; }

    public int getEXP() { return exp; }

    private void setEXP(int value) { this.exp = Math.min(Math.max(0, value), 100); }

    public List<String> getActions() { return actions; }

}
