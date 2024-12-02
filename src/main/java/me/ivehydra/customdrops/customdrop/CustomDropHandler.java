package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.condition.ConditionHandler;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class CustomDropHandler {

    protected boolean enabled;
    protected boolean disableVanillaDrops;
    protected List<String> vanillaDropsConditions;
    protected boolean autoPickup;
    protected boolean disableVanillaEXP;
    protected List<String> vanillaEXPConditions;
    protected final List<CustomDrop> customDrops;
    protected ConditionHandler conditionHandler;

    public CustomDropHandler(boolean enabled, boolean disableVanillaDrops, List<String> vanillaDropsConditions, boolean autoPickup, boolean disableVanillaEXP, List<String> vanillaEXPConditions, List<CustomDrop> customDrops) {
        this.enabled = enabled;
        this.disableVanillaDrops = disableVanillaDrops;
        this.vanillaDropsConditions = vanillaDropsConditions;
        this.autoPickup = autoPickup;
        this.disableVanillaEXP = disableVanillaEXP;
        this.vanillaEXPConditions = vanillaEXPConditions;
        this.customDrops = customDrops;
        this.conditionHandler = new ConditionHandler();
    }

    public boolean isEnabled() { return enabled; }

    public boolean isVanillaDropsDisabled() { return disableVanillaDrops; }

    public boolean areDropsConditionsTrue(Player p) { return conditionHandler.handle(p, vanillaDropsConditions); }

    public boolean isAutoPickupEnabled() { return autoPickup; }

    public boolean isVanillaEXPDisabled() { return disableVanillaEXP; }

    public boolean areEXPConditionsTrue(Player p) { return conditionHandler.handle(p, vanillaEXPConditions); }

    public List<CustomDrop> getCustomDrops() { return customDrops; }

}
