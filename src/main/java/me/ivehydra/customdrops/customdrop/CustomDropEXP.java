package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.condition.ConditionHandler;
import me.ivehydra.customdrops.customdrop.multiplier.Multiplier;
import org.bukkit.entity.Player;

import java.util.List;

public class CustomDropEXP {

    private List<String> conditions;
    private double chance;
    private boolean disableForNatural;
    private boolean disableForPlaced;
    private boolean disableForSpawner;
    private boolean disableForSpawnerEgg;
    private Multiplier expMultiplier;
    private int exp;
    private List<String> actions;

    public CustomDropEXP(List<String> conditions, double chance, boolean disableForNatural, boolean disableForPlaced, Multiplier expMultiplier, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForPlaced = disableForPlaced;
        this.expMultiplier = expMultiplier;
        setEXP(exp);
        this.actions = actions;
    }

    public CustomDropEXP(List<String> conditions, double chance, boolean disableForNatural, boolean disableForSpawner, boolean disableForSpawnerEgg, Multiplier expMultiplier, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        this.disableForNatural = disableForNatural;
        this.disableForSpawner = disableForSpawner;
        this.disableForSpawnerEgg = disableForSpawnerEgg;
        this.expMultiplier = expMultiplier;
        setEXP(exp);
        this.actions = actions;
    }

    public CustomDropEXP(List<String> conditions, double chance, Multiplier expMultiplier, int exp, List<String> actions) {
        this.conditions = conditions;
        setChance(chance);
        this.expMultiplier = expMultiplier;
        setEXP(exp);
        this.actions = actions;
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

    public Multiplier getEXPMultiplier() { return expMultiplier; }

    public int getEXP() { return exp; }

    private void setEXP(int value) {
        exp += value;
        exp = Math.min(Math.max(0, exp), 100);
    }

    public List<String> getActions() { return actions; }

}
