package me.ivehydra.customdrops.customdrop.multiplier;

public class Multiplier {

    private boolean disabled;
    private double chance;
    private double exp;

    public Multiplier(boolean disabled, double chance, double exp) {
        this.disabled = disabled;
        setChance(chance);
        setEXP(exp);
    }

    public boolean isDisabled() { return disabled; }

    public double getChance() { return chance; }

    public double getExp() { return exp; }

    private void setChance(double chance) { this.chance = Math.min(Math.max(0, chance), 100); }

    private void setEXP(double exp) { this.exp = Math.min(Math.max(0, exp), 100); }

}
