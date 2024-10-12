package me.ivehydra.customdrops.customdrop;

public enum CustomDropType {

    ITEM,
    ITEMS;

    public static CustomDropType fromString(String type) {
        for(CustomDropType dropType : values())
            if(dropType.name().equalsIgnoreCase(type)) return dropType;
        throw new IllegalArgumentException("[CustomDrops] Unknown CustomDropType: " + type);
    }

}
