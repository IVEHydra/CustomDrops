package me.ivehydra.customdrops.customdrop.handlers;

import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropHandler;

import java.util.List;

public class CustomDropEntity extends CustomDropHandler {

    public CustomDropEntity(boolean enabled, boolean disableVanillaDrops, List<String> vanillaDropsConditions, boolean autoPickup, boolean disableVanillaEXP, List<String> vanillaEXPConditions, List<CustomDrop> customDrops) {
        super(enabled, disableVanillaDrops, vanillaDropsConditions, autoPickup, disableVanillaEXP, vanillaEXPConditions, customDrops);
    }

}
