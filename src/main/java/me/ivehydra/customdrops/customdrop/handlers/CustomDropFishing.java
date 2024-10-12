package me.ivehydra.customdrops.customdrop.handlers;

import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropHandler;

import java.util.List;

public class CustomDropFishing extends CustomDropHandler {

    public CustomDropFishing(boolean enabled, boolean disableVanillaDrops, List<String> vanillaDropsWorlds, boolean disableVanillaEXP, List<String> vanillaEXPWorlds, List<CustomDrop> customDrops) {
        super(enabled, disableVanillaDrops, vanillaDropsWorlds, disableVanillaEXP, vanillaEXPWorlds, customDrops);
    }

}
