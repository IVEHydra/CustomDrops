package me.ivehydra.customdrops.customdrop;

import net.md_5.bungee.api.ChatColor;

public enum CustomDropType {

    ITEM,
    ITEMS;

    public static CustomDropType fromString(String type) {
        for(CustomDropType dropType : values())
            if(dropType.name().equalsIgnoreCase(type)) return dropType;
        throw new IllegalArgumentException("[CustomDrops]" + ChatColor.RED + " Unknown CustomDropType: " + type);
    }

}
