package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropEXP;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropEntity;
import me.ivehydra.customdrops.customdrop.multiplier.Multiplier;
import me.ivehydra.customdrops.utils.EnchantmentUtils;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

            if(customDropEntity.isVanillaDropsDisabled() && customDropEntity.areDropsConditionsTrue(p))
                e.getDrops().clear();
            else {
                for(ItemStack drop : e.getDrops()) {
                    if(customDropEntity.isAutoPickupEnabled())
                        if(addItem(p, drop))
                            e.getDrops().clear();

                }
            }

            if(customDropEntity.isVanillaEXPDisabled() && customDropEntity.areEXPConditionsTrue(p)) e.setDroppedExp(0);

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
                Multiplier chanceMultiplier = customDrop.getChanceMultiplier();
                ItemStack itemStack;
                if(VersionUtils.isAtLeastVersion19()) itemStack = p.getInventory().getItemInMainHand();
                else itemStack = p.getItemInHand();

                if(!chanceMultiplier.isDisabled()) {
                    int lootingLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LOOTING);
                    double percentagePerLevel = chanceMultiplier.getValue();
                    chance += chance * (lootingLevel * percentagePerLevel);
                }

                if(random.nextDouble() < chance) {
                    World world = entity.getWorld();
                    Location loc = entity.getLocation();
                    switch(customDrop.getType()) {
                        case ITEM:
                            ItemStack drop = customDrop.getItemStack();
                            if(customDrop.isAutoPickupEnabled()) {
                                if(!addItem(p, drop))
                                    world.dropItemNaturally(loc, drop);
                            } else
                                world.dropItemNaturally(loc, drop);
                            break;
                        case ITEMS:
                            for(ItemStack drops : customDrop.getItemStacks()) {
                                if(customDrop.isAutoPickupEnabled()) {
                                    if(!addItem(p, drops))
                                        world.dropItemNaturally(loc, drops);
                                } else
                                    world.dropItemNaturally(loc, drops);
                            }
                            break;
                    }

                    instance.getActionManager().execute(p, customDrop.getActions());

                    for(CustomDropEXP customDropEXP : customDrop.getCustomDropEXPs()) {

                        if(customDropEXP.isForNaturalDisabled() && isNatural(entity))
                            continue;
                        if(customDropEXP.isForSpawnerDisabled() && isSpawner(entity))
                            continue;
                        if(customDropEXP.isForSpawnerEggDisabled() && isSpawnerEgg(entity))
                            continue;

                        if(!customDropEXP.areConditionsTrue(p)) continue;

                        double expChance = customDropEXP.getChance();
                        Multiplier expMultiplier = customDropEXP.getEXPMultiplier();

                        if(!expMultiplier.isDisabled()) {
                            int lootingLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LOOTING);
                            double percentagePerLevel = chanceMultiplier.getValue();
                            chance += chance * (lootingLevel * percentagePerLevel);
                        }

                        if(random.nextDouble() < expChance) {
                            p.giveExp(customDropEXP.getEXP());
                            instance.getActionManager().execute(p, customDropEXP.getActions());
                        }

                    }

                }

            }

            if(isNatural(entity)) instance.getNaturalEntities().remove(entity.getUniqueId());
            if(isSpawner(entity)) instance.getSpawnerEntities().remove(entity.getUniqueId());
            if(isSpawnerEgg(entity)) instance.getSpawnerEggEntities().remove(entity.getUniqueId());

        }
    }

    private boolean addItem(Player p, ItemStack itemStack) {
        PlayerInventory inv = p.getInventory();
        if(inv.firstEmpty() == -1) {
            p.sendMessage(MessageUtils.INVENTORY_FULL.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            return false;
        }
        inv.addItem(itemStack);
        return true;
    }

    private boolean isNatural(Entity entity) { return instance.getNaturalEntities().contains(entity.getUniqueId()); }

    private boolean isSpawner(Entity entity) { return instance.getSpawnerEntities().contains(entity.getUniqueId()); }

    private boolean isSpawnerEgg(Entity entity) { return instance.getSpawnerEggEntities().contains(entity.getUniqueId()); }

}
