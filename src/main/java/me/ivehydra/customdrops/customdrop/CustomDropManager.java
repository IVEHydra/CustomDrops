package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropBlock;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropEntity;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropFishing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomDropManager {

    private final CustomDrops instance = CustomDrops.getInstance();
    private final Map<String, CustomDropBlock> blockCustomDrops;
    private final Map<String, CustomDropEntity> entityCustomDrops;
    private CustomDropFishing fishingCustomDrops;
    private CustomDropSettings settings;

    public CustomDropManager() {
        blockCustomDrops = new HashMap<>();
        entityCustomDrops = new HashMap<>();
        fishingCustomDrops = loadFishingCustomDrop();
        settings = loadSettings();

        load();
    }

    private CustomDropSettings loadSettings() {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("customDropsSettings");
        List<String> fortuneMultiplierWorlds = new ArrayList<>();
        List<String> lootingMultiplierWorlds = new ArrayList<>();
        List<String> luckOfTheSeaWorlds = new ArrayList<>();
        if(section != null) {
            boolean fortuneMultiplier = section.getBoolean("fortuneMultiplier.enabled");
            fortuneMultiplierWorlds = section.getStringList("fortuneMultiplier.worlds");
            double fortuneMultiplierPerLevel = section.getDouble("fortuneMultiplier.percentagePerLevel");
            boolean lootingMultiplier = section.getBoolean("lootingMultiplier.enabled");
            lootingMultiplierWorlds = section.getStringList("lootingMultiplier.worlds");
            double lootingMultiplierPerLevel = section.getDouble("lootingMultiplier.percentagePerLevel");
            boolean luckOfTheSea = section.getBoolean("luckOfTheSeaMultiplier.enabled");
            luckOfTheSeaWorlds = section.getStringList("luckOfTheSeaMultiplier.worlds");
            double luckMultiplierPerLevel = section.getDouble("luckOfTheSeaMultiplier.percentagePerLevel");

            return new CustomDropSettings(fortuneMultiplier, fortuneMultiplierWorlds, fortuneMultiplierPerLevel, lootingMultiplier, lootingMultiplierWorlds, lootingMultiplierPerLevel, luckOfTheSea, luckOfTheSeaWorlds, luckMultiplierPerLevel);
        }
        return new CustomDropSettings(false, fortuneMultiplierWorlds, 0.0, false, lootingMultiplierWorlds, 0.0, false, luckOfTheSeaWorlds, 0.0);
    }

    private void load() {
        ConfigurationSection section = instance.getCustomDropsFile().getConfigurationSection("customDrops");
        if(section == null) return;

        ConfigurationSection blocksSection = section.getConfigurationSection("blocks");
        if(blocksSection != null) {
            for(String block : blocksSection.getKeys(false)) {
                ConfigurationSection blockSection = blocksSection.getConfigurationSection(block);
                if(blockSection != null) {
                    boolean enabled = blockSection.getBoolean("enabled");
                    boolean disableVanillaDrops = blockSection.getBoolean("vanillaDrops.disabled");
                    List<String> vanillaDropsWorlds = blockSection.getStringList("vanillaDrops.worlds");
                    boolean disableVanillaEXP = blockSection.getBoolean("vanillaEXP.disabled");
                    List<String> vanillaEXPWorlds = blockSection.getStringList("vanillaEXP.worlds");
                    List<CustomDrop> customDrops = loadCustomDrops(blockSection.getConfigurationSection("drops"), true, false);
                    CustomDropBlock customDropBlock = new CustomDropBlock(enabled, disableVanillaDrops, vanillaDropsWorlds, disableVanillaEXP, vanillaEXPWorlds, customDrops);
                    blockCustomDrops.put(block, customDropBlock);
                }
            }
        }

        ConfigurationSection entitiesSection = section.getConfigurationSection("entities");
        if(entitiesSection != null) {
            for(String entity : entitiesSection.getKeys(false)) {
                ConfigurationSection entitySection = entitiesSection.getConfigurationSection(entity);
                if(entitySection != null) {
                    boolean enabled = entitySection.getBoolean("enabled");
                    boolean disableVanillaDrops = entitySection.getBoolean("vanillaDrops.disabled");
                    List<String> vanillaDropsWorlds = entitySection.getStringList("vanillaDrops.worlds");
                    boolean disableVanillaEXP = entitySection.getBoolean("vanillaEXP.disabled");
                    List<String> vanillaEXPWorlds = entitySection.getStringList("vanillaEXP.worlds");
                    List<CustomDrop> customDrops = loadCustomDrops(entitySection.getConfigurationSection("drops"), false, false);
                    CustomDropEntity customDropEntity = new CustomDropEntity(enabled, disableVanillaDrops, vanillaDropsWorlds, disableVanillaEXP, vanillaEXPWorlds, customDrops);
                    entityCustomDrops.put(entity, customDropEntity);
                }
            }
        }
    }

    private CustomDropFishing loadFishingCustomDrop() {
        ConfigurationSection section = instance.getCustomDropsFile().getConfigurationSection("customDrops");
        List<String> vanillaDropsWorlds = new ArrayList<>();
        List<String> vanillaEXPWorlds = new ArrayList<>();
        List<CustomDrop> customDrops = new ArrayList<>();
        if(section != null) {
            ConfigurationSection fishingSection = section.getConfigurationSection("fishing");
            if(fishingSection != null) {
                boolean enabled = fishingSection.getBoolean("enabled");
                boolean disableVanillaDrops = fishingSection.getBoolean("vanillaDrops.disabled");
                vanillaDropsWorlds = fishingSection.getStringList("vanillaDrops.worlds");
                boolean disableVanillaEXP = fishingSection.getBoolean("vanillaEXP.disabled");
                vanillaEXPWorlds = fishingSection.getStringList("vanillaEXP.worlds");
                customDrops = loadCustomDrops(fishingSection.getConfigurationSection("drops"), false, true);
                return new CustomDropFishing(enabled, disableVanillaDrops, vanillaDropsWorlds, disableVanillaEXP, vanillaEXPWorlds, customDrops);
            }
        }
        return new CustomDropFishing(false, false, vanillaDropsWorlds, false, vanillaEXPWorlds, customDrops);
    }

    private List<CustomDrop> loadCustomDrops(ConfigurationSection section, boolean isBlock, boolean isFishing) {
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
                int exp = dropSection.getInt("exp");
                CustomDrop customDrop;

                if(isBlock && !isFishing) customDrop = new CustomDrop(conditions, chance, disableForNatural, disableForPlaced, drop, type, itemStack, itemStacks, actions, exp);
                else if(!isBlock && !isFishing) customDrop = new CustomDrop(conditions, chance, disableForNatural, disableForSpawner, disableForSpawnerEgg, drop, type, itemStack, itemStacks, actions, exp);
                else customDrop = new CustomDrop(conditions, chance, drop, type, itemStack, itemStacks, actions, exp);

                customDrops.add(customDrop);
            }
        }
        return customDrops;
    }

    public Map<String, CustomDropBlock> getBlockCustomDrops() { return blockCustomDrops; }

    public List<String> getBlockNames() { return new ArrayList<>(blockCustomDrops.keySet()); }

    public Map<String, CustomDropEntity> getEntityCustomDrops() { return entityCustomDrops; }

    public List<String> getEntityNames() { return new ArrayList<>(entityCustomDrops.keySet()); }

    public CustomDropFishing getFishingCustomDrop() { return fishingCustomDrops; }

    public CustomDropSettings getSettings() { return settings; }

}
