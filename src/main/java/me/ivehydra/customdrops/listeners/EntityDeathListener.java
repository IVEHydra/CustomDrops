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
import java.util.UUID;

public class EntityDeathListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player p = entity.getKiller();

        if(p == null) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        UUID uuid = entity.getUniqueId();
        CustomDropEntity customDropEntity;

        if(isMythic(entity)) {
            String mobType = instance.getMythicEntities().get(uuid);
            customDropEntity = customDropManager.getEntityCustomDrops().get(mobType);
        } else customDropEntity = customDropManager.getEntityCustomDrops().get(entity.getType().toString());

        if(customDropEntity != null) {

            if(!customDropEntity.isEnabled()) return;

            if(customDropEntity.isVanillaDropsDisabled() && customDropEntity.areDropsConditionsTrue(p))
                e.getDrops().clear();
            else
                for(ItemStack drops : e.getDrops()) {
                    if(customDropEntity.isAutoPickupEnabled())
                        addItem(p, drops, entity, true, e);
                }


            if(customDropEntity.isVanillaEXPDisabled() && customDropEntity.areEXPConditionsTrue(p)) e.setDroppedExp(0);

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

                Random random = new Random();

                if(random.nextDouble() < chance) {
                    World world = entity.getWorld();
                    Location loc = entity.getLocation();
                    switch(customDrop.getType()) {
                        case ITEM:
                            ItemStack drop = customDrop.getItemStack();
                            if(customDrop.isAutoPickupEnabled())
                                addItem(p, drop, entity, false, e);
                            else
                                world.dropItemNaturally(loc, drop);
                            break;
                        case ITEMS:
                            for(ItemStack drops : customDrop.getItemStacks()) {
                                if(customDrop.isAutoPickupEnabled())
                                    addItem(p, drops, entity, false, e);
                                else
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
                        Multiplier expChanceMultiplier = customDropEXP.getChanceMultiplier();

                        if(!expChanceMultiplier.isDisabled()) {
                            int lootingLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LOOTING);
                            double percentagePerLevel = expChanceMultiplier.getValue();
                            expChance += expChance * (lootingLevel * percentagePerLevel);
                        }

                        int exp = customDropEXP.getEXP();
                        Multiplier expMultiplier = customDropEXP.getEXPMultiplier();

                        if(!expMultiplier.isDisabled()) {
                            int lootingLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LOOTING);
                            double percentagePerLevel = expMultiplier.getValue();
                            exp += (int) (exp * (lootingLevel * percentagePerLevel));
                        }

                        if(random.nextDouble() < expChance) {
                            p.giveExp(exp);
                            instance.getActionManager().execute(p, customDropEXP.getActions());
                        }

                    }

                }

            }

            if(isMythic(entity)) instance.getMythicEntities().remove(uuid);
            if(isNatural(entity)) instance.getNaturalEntities().remove(uuid);
            if(isSpawner(entity)) instance.getSpawnerEntities().remove(uuid);
            if(isSpawnerEgg(entity)) instance.getSpawnerEggEntities().remove(uuid);

        }
    }

    private void addItem(Player p, ItemStack itemStack, Entity entity, boolean clear, EntityDeathEvent e) {
        PlayerInventory inv = p.getInventory();
        int add = itemStack.getAmount();
        ItemStack clone = itemStack.clone();

        for(int i = 0; i < 36; i++) {
            if(add == 0)
                break;
            ItemStack slot = inv.getItem(i);
            if(slot != null && slot.isSimilar(itemStack)) {
                int space = slot.getMaxStackSize() - slot.getAmount();
                if(space > 0) {
                    int toAdd = Math.min(space, add);
                    slot.setAmount(slot.getAmount() + toAdd);
                    add -= toAdd;
                }
            }
        }

        if(add > 0) {
            for(int i = 0; i < 36; i++) {
                if(add == 0)
                    break;
                ItemStack slot = inv.getItem(i);
                if(slot == null) {
                    clone.setAmount(add);
                    inv.setItem(i, clone);
                    add = 0;
                    break;
                }
            }
        }

        if(add > 0) {
            World world = entity.getWorld();
            Location loc = entity.getLocation();
            clone.setAmount(add);
            world.dropItemNaturally(loc, clone);
            p.sendMessage(MessageUtils.INVENTORY_FULL.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
        }

        if(clear)
            e.getDrops().clear();

    }

    private boolean isMythic(Entity entity) { return instance.getMythicEntities().containsKey(entity.getUniqueId()); }

    private boolean isNatural(Entity entity) { return instance.getNaturalEntities().contains(entity.getUniqueId()); }

    private boolean isSpawner(Entity entity) { return instance.getSpawnerEntities().contains(entity.getUniqueId()); }

    private boolean isSpawnerEgg(Entity entity) { return instance.getSpawnerEggEntities().contains(entity.getUniqueId()); }

}
