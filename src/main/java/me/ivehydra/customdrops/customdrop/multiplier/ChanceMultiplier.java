package me.ivehydra.customdrops.customdrop.multiplier;

public class ChanceMultiplier {

    private boolean disabled;
    private double value;

    public ChanceMultiplier(boolean disabled, double value) {
        this.disabled = disabled;
        setValue(value);
    }

    public boolean isDisabled() { return disabled; }

    public double getValue() { return value; }

    private void setValue(double value) { this.value = Math.min(Math.max(0, value), 100); }

}
