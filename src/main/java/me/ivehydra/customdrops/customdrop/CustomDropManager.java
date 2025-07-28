package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropBlock;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropEntity;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropFishing;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropPiglinBartering;
import me.ivehydra.customdrops.customdrop.multiplier.ChanceMultiplier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomDropManager {

    private final CustomDrops instance = CustomDrops.getInstance();
    private CustomDropSettings customDropSettings;
    private final Map<String, CustomDropBlock> blockCustomDrops;
    private final Map<String, CustomDropEntity> entityCustomDrops;
    private CustomDropFishing fishingCustomDrops;
    private CustomDropPiglinBartering barteringCustomDrops;

    public CustomDropManager() {
        customDropSettings = loadSettings();
        blockCustomDrops = new HashMap<>();
        entityCustomDrops = new HashMap<>();
        fishingCustomDrops = loadFishingCustomDrops();
        barteringCustomDrops = loadPiglinBarteringCustomDrops();

        loadBlocksCustomDrops();
        loadEntitiesCustomDrops();
    }

    private CustomDropSettings loadSettings() {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("customDropsSettings");
        List<String> worlds = new ArrayList<>();
        if(section != null) {
            boolean enabled = section.getBoolean("silkTouch.enabled");
            worlds = section.getStringList("silkTouch.worlds");
            return new CustomDropSettings(enabled, worlds);
        }
        return new CustomDropSettings(false, worlds);
    }

    private void loadBlocksCustomDrops() {
        ConfigurationSection section = instance.getCustomDropsFile().getConfigurationSection("customDrops");
        if(section == null) return;

        ConfigurationSection blocksSection = section.getConfigurationSection("blocks");
        if(blocksSection != null) {
            for(String block : blocksSection.getKeys(false)) {
                ConfigurationSection blockSection = blocksSection.getConfigurationSection(block);
                if(blockSection != null) {
                    boolean enabled = blockSection.getBoolean("enabled");
                    boolean disableVanillaDrops = blockSection.getBoolean("vanillaDrops.disabled");
                    List<String> vanillaDropsConditions = blockSection.getStringList("vanillaDrops.conditions");
                    boolean autoPickup = blockSection.getBoolean("vanillaDrops.enableAutoPickup");
                    boolean disableVanillaEXP = blockSection.getBoolean("vanillaEXP.disabled");
                    List<String> vanillaEXPConditions = blockSection.getStringList("vanillaEXP.conditions");
                    List<CustomDrop> customDrops = loadCustomDrops(blockSection.getConfigurationSection("drops"), true, false, false);
                    CustomDropBlock customDropBlock = new CustomDropBlock(enabled, disableVanillaDrops, vanillaDropsConditions, autoPickup, disableVanillaEXP, vanillaEXPConditions, customDrops);
                    blockCustomDrops.put(block, customDropBlock);
                }
            }
        }
    }

    private void loadEntitiesCustomDrops() {
        ConfigurationSection section = instance.getCustomDropsFile().getConfigurationSection("customDrops");
        if(section == null) return;

        ConfigurationSection entitiesSection = section.getConfigurationSection("entities");
        if(entitiesSection != null) {
            for(String entity : entitiesSection.getKeys(false)) {
                ConfigurationSection entitySection = entitiesSection.getConfigurationSection(entity);
                if(entitySection != null) {
                    boolean enabled = entitySection.getBoolean("enabled");
                    boolean disableVanillaDrops = entitySection.getBoolean("vanillaDrops.disabled");
                    List<String> vanillaDropsConditions = entitySection.getStringList("vanillaDrops.conditions");
                    boolean autoPickup = entitySection.getBoolean("vanillaDrops.enableAutoPickup");
                    boolean disableVanillaEXP = entitySection.getBoolean("vanillaEXP.disabled");
                    List<String> vanillaEXPConditions = entitySection.getStringList("vanillaEXP.conditions");
                    List<CustomDrop> customDrops = loadCustomDrops(entitySection.getConfigurationSection("drops"), false, false, false);
                    CustomDropEntity customDropEntity = new CustomDropEntity(enabled, disableVanillaDrops, vanillaDropsConditions, autoPickup, disableVanillaEXP, vanillaEXPConditions, customDrops);
                    entityCustomDrops.put(entity, customDropEntity);
                }
            }
        }
    }

    private CustomDropFishing loadFishingCustomDrops() {
        ConfigurationSection section = instance.getCustomDropsFile().getConfigurationSection("customDrops");
        List<String> vanillaDropsConditions = new ArrayList<>();
        List<String> vanillaEXPConditions = new ArrayList<>();
        List<CustomDrop> customDrops = new ArrayList<>();
        if(section != null) {
            ConfigurationSection fishingSection = section.getConfigurationSection("fishing");
            if(fishingSection != null) {
                boolean enabled = fishingSection.getBoolean("enabled");
                boolean disableVanillaDrops = fishingSection.getBoolean("vanillaDrops.disabled");
                vanillaDropsConditions = fishingSection.getStringList("vanillaDrops.conditions");
                boolean autoPickup = fishingSection.getBoolean("vanillaDrops.enableAutoPickup");
                boolean disableVanillaEXP = fishingSection.getBoolean("vanillaEXP.disabled");
                vanillaEXPConditions = fishingSection.getStringList("vanillaEXP.conditions");
                customDrops = loadCustomDrops(fishingSection.getConfigurationSection("drops"), false, true, false);
                return new CustomDropFishing(enabled, disableVanillaDrops, vanillaDropsConditions, autoPickup, disableVanillaEXP, vanillaEXPConditions, customDrops);
            }
        }
        return new CustomDropFishing(false, false, vanillaDropsConditions, false,false, vanillaEXPConditions, customDrops);
    }

    private CustomDropPiglinBartering loadPiglinBarteringCustomDrops() {
        ConfigurationSection section = instance.getCustomDropsFile().getConfigurationSection("customDrops");
        List<String> vanillaDropsConditions = new ArrayList<>();
        List<CustomDrop> customDrops = new ArrayList<>();
        if(section != null) {
            ConfigurationSection barteringSection = section.getConfigurationSection("piglinBartering");
            if(barteringSection != null) {
                boolean enabled = barteringSection.getBoolean("enabled");
                boolean disableVanillaDrops = barteringSection.getBoolean("vanillaDrops.disabled");
                vanillaDropsConditions = barteringSection.getStringList("vanillaDrops.conditions");
                boolean autoPickup = barteringSection.getBoolean("vanillaDrops.enableAutoPickup");
                customDrops = loadCustomDrops(barteringSection.getConfigurationSection("drops"), false, true, true);
                return new CustomDropPiglinBartering(enabled, disableVanillaDrops, vanillaDropsConditions, autoPickup, false, Collections.emptyList(), customDrops);
            }
        }
        return new CustomDropPiglinBartering(false, false, vanillaDropsConditions, false, false, Collections.emptyList(), customDrops);
    }

    private List<CustomDrop> loadCustomDrops(ConfigurationSection section, boolean isBlock, boolean isFishing, boolean isBartering) {
        List<CustomDrop> customDrops = new ArrayList<>();
        if(section == null) return customDrops;

        for(String drop : section.getKeys(false)) {
            ConfigurationSection dropSection = section.getConfigurationSection(drop);
            if(dropSection != null) {

                List<String> conditions = dropSection.getStringList("conditions");
                double chance = dropSection.getDouble("chance");
                boolean disableForNatural = !isFishing && dropSection.getBoolean("disableForNatural");
                boolean disableForPlaced = !isFishing && isBlock && dropSection.getBoolean("disableForPlaced");
                boolean disableForSpawner = !isFishing && !isBlock && dropSection.getBoolean("disableForSpawner");
                boolean disableForSpawnerEgg = !isFishing && !isBlock && dropSection.getBoolean("disableForSpawnerEgg");
                String enchantment;
                String expEnchantment;

                if(isBlock && !isFishing) {
                    enchantment = "fortuneChanceMultiplier";
                    expEnchantment = "fortuneEXPMultiplier";
                } else if(!isBlock && !isFishing) {
                    enchantment = "lootingChanceMultiplier";
                    expEnchantment = "lootingEXPMultiplier";
                } else {
                    enchantment = "luckChanceMultiplier";
                    expEnchantment = "luckEXPMultiplier";
                }

                ChanceMultiplier chanceMultiplier = new ChanceMultiplier(section.getBoolean(enchantment + ".disabled"), section.getDouble(enchantment + ".percentagePerLevel"));
                boolean autoPickup = dropSection.getBoolean("enableAutoPickup");
                CustomDropType type = CustomDropType.fromString(dropSection.getString("drop.type"));
                ItemStack itemStack = null;
                List<ItemStack> itemStacks = new ArrayList<>();

                switch(type) {
                    case ITEM:
                        itemStack = dropSection.getItemStack("drop.item");
                        break;
                    case ITEMS:
                        ConfigurationSection itemsSection = dropSection.getConfigurationSection("drop.items");
                        if(itemsSection != null) {
                            for(String name : itemsSection.getKeys(false)) {
                                ItemStack serialized = itemsSection.getItemStack(name);
                                if(serialized != null) itemStacks.add(serialized);
                            }
                        }
                        break;
                }

                List<String> actions = dropSection.getStringList("drop.actions");
                List<CustomDropEXP> customDropEXPs = new ArrayList<>();
                ConfigurationSection expSection = dropSection.getConfigurationSection("exp");

                if(expSection != null) {
                    for(String key : expSection.getKeys(false)) {
                        ConfigurationSection configuration = expSection.getConfigurationSection(key);
                        if(configuration != null) {
                            List<String> expConditions = configuration.getStringList("conditions");
                            double expChance = configuration.getDouble("chance");
                            boolean expDisableForNatural = !isFishing && configuration.getBoolean("disableForNatural");
                            boolean expDisableForPlaced = !isFishing && isBlock && configuration.getBoolean("disableForPlaced");
                            boolean expDisableForSpawner = !isFishing && !isBlock && configuration.getBoolean("disableForSpawner");
                            boolean expDisableForSpawnerEgg = !isFishing && !isBlock && configuration.getBoolean("disableForSpawnerEgg");
                            ChanceMultiplier expChanceMultiplier = new ChanceMultiplier(configuration.getBoolean(enchantment + ".disabled"), configuration.getDouble(enchantment + ".percentagePerLevel"));
                            ChanceMultiplier expMultiplier = new ChanceMultiplier(configuration.getBoolean(expEnchantment + ".disabled"), configuration.getDouble(expEnchantment + ".percentagePerLevel"));
                            int exp = configuration.getInt("exp");
                            List<String> expActions = configuration.getStringList("actions");
                            CustomDropEXP customDropEXP;

                            if(isBlock && !isFishing && !isBartering) customDropEXP = new CustomDropEXP(expConditions, expChance, expDisableForNatural, expDisableForPlaced, expChanceMultiplier, expMultiplier, exp, expActions);
                            else if(!isBlock && !isFishing && !isBartering) customDropEXP = new CustomDropEXP(expConditions, expChance, expDisableForNatural, expDisableForSpawner, expDisableForSpawnerEgg, expChanceMultiplier, expMultiplier, exp, expActions);
                            else if(!isBlock && isFishing && !isBartering) customDropEXP = new CustomDropEXP(expConditions, expChance, expChanceMultiplier, expMultiplier, exp, expActions);
                            else customDropEXP = new CustomDropEXP(expConditions, expChance, exp, expActions);

                            customDropEXPs.add(customDropEXP);
                        }
                    }
                }

                CustomDrop customDrop;
                if(isBlock && !isFishing && !isBartering) customDrop = new CustomDrop(drop, conditions, chance, disableForNatural, disableForPlaced, chanceMultiplier, autoPickup, type, itemStack, itemStacks, actions, customDropEXPs);
                else if(!isBlock && !isFishing && !isBartering) customDrop = new CustomDrop(drop, conditions, chance, disableForNatural, disableForSpawner, disableForSpawnerEgg, chanceMultiplier, autoPickup, type, itemStack, itemStacks, actions, customDropEXPs);
                else if(!isBlock && isFishing && !isBartering) customDrop = new CustomDrop(drop, conditions, chance, chanceMultiplier, autoPickup, type, itemStack, itemStacks, actions, customDropEXPs);
                else customDrop = new CustomDrop(drop, conditions, chance, autoPickup, type, itemStack, itemStacks, actions, customDropEXPs);

                customDrops.add(customDrop);
            }
        }
        return customDrops;
    }

    public CustomDropSettings getCustomDropSettings() { return customDropSettings; }

    public Map<String, CustomDropBlock> getBlockCustomDrops() { return blockCustomDrops; }

    public List<String> getBlockNames() { return new ArrayList<>(blockCustomDrops.keySet()); }

    public Map<String, CustomDropEntity> getEntityCustomDrops() { return entityCustomDrops; }

    public List<String> getEntityNames() { return new ArrayList<>(entityCustomDrops.keySet()); }

    public CustomDropFishing getFishingCustomDrops() { return fishingCustomDrops; }

    public CustomDropPiglinBartering getBarteringCustomDrops() { return barteringCustomDrops; }

}
