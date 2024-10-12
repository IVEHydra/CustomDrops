package me.ivehydra.customdrops.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum EnchantmentUtils {

    FORTUNE("LOOT_BONUS_BLOCKS", "BLOCKS_LOOT_BONUS", "FORT"),
    LOOTING("LOOT_BONUS_MOBS", "MOBS_LOOT_BONUS", "LOOT"),
    LUCK("LUCK", "LUCK_OF_SEA", "LUCK_OF_SEAS", "ROD_LUCK"),
    SILK_TOUCH("SOFT_TOUCH");

    private final String[] aliases;

    EnchantmentUtils(String... aliases) { this.aliases = aliases; }

    private Enchantment getEnchantment() {
        for(String alias : aliases) {
            Enchantment enchantment;
            if(VersionUtils.isAtLeastVersion113()) enchantment = Enchantment.getByKey(NamespacedKey.minecraft(alias.toLowerCase()));
            else enchantment = Enchantment.getByName(alias);
            if(enchantment != null) return enchantment;
        }
        return null;
    }

    public static int getEnchantmentLevel(ItemStack itemStack, EnchantmentUtils enchantmentEnum) {
        Enchantment enchantment = enchantmentEnum.getEnchantment();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemStack.getType() != Material.AIR && enchantment != null && itemMeta != null && itemMeta.hasEnchant(enchantment))
            return itemStack.getEnchantmentLevel(enchantment);
        return 0;
    }

}
