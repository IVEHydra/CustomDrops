package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.CustomDropSettings;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropEntity;
import me.ivehydra.customdrops.customdrop.multiplier.MultiplierType;
import me.ivehydra.customdrops.utils.EnchantmentUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EntityDeathListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player p = entity.getKiller();

        if(p == null) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropEntity customDropEntity = customDropManager.getEntityCustomDrops().get(entity.getType().toString());

        if(customDropEntity != null) {

            if(!customDropEntity.isEnabled()) return;

            if(customDropEntity.isVanillaDropsDisabled() && customDropEntity.getVanillaDropsWorlds().contains(p.getWorld())) e.getDrops().clear();

            if(customDropEntity.isVanillaEXPDisabled() && customDropEntity.getVanillaEXPWorlds().contains(p.getWorld())) e.setDroppedExp(0);

            Random random = new Random();

            for(CustomDrop customDrop : customDropEntity.getCustomDrops()) {

                if(customDrop.isForNaturalDisabled() && isNatural(entity))
                    continue;

                if(customDrop.isForSpawnerDisabled() && isSpawner(entity))
                    continue;

                if(customDrop.isForSpawnerEggDisabled() && isSpawnerEgg(entity))
                    continue;

                if(!customDrop.areConditionsTrue(p)) continue;

                double chance = customDrop.getChance();
                CustomDropSettings settings = customDropManager.getSettings();

                if(settings.isEnabled(MultiplierType.LOOTING) && settings.getWorlds(MultiplierType.LOOTING).contains(p.getWorld())) {
                    ItemStack itemStack;
                    if(VersionUtils.isAtLeastVersion19()) itemStack = p.getInventory().getItemInMainHand();
                    else itemStack = p.getItemInHand();
                    int lootingLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LOOTING);
                    double percentagePerLevel = settings.getLevel(MultiplierType.LOOTING);
                    chance += chance * (lootingLevel * percentagePerLevel);
                }

                if(random.nextDouble() < chance) {
                    switch(customDrop.getType()) {
                        case ITEM:
                            entity.getWorld().dropItemNaturally(entity.getLocation(), customDrop.getItemStack());
                            break;
                        case ITEMS:
                            for(ItemStack drop : customDrop.getItemStacks())
                                entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
                            break;
                    }

                    instance.getActionManager().execute(p, customDrop.getActions());
                    p.giveExp(customDrop.getExp());
                }

            }

            if(isNatural(entity)) instance.getNaturalEntities().remove(entity.getUniqueId());
            if(isSpawner(entity)) instance.getSpawnerEntities().remove(entity.getUniqueId());
            if(isSpawnerEgg(entity)) instance.getSpawnerEggEntities().remove(entity.getUniqueId());

        }
    }

    private boolean isNatural(Entity entity) { return instance.getNaturalEntities().contains(entity.getUniqueId()); }

    private boolean isSpawner(Entity entity) { return instance.getSpawnerEntities().contains(entity.getUniqueId()); }

    private boolean isSpawnerEgg(Entity entity) { return instance.getSpawnerEggEntities().contains(entity.getUniqueId()); }

}
