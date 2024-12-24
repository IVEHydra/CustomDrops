package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropEXP;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropFishing;
import me.ivehydra.customdrops.customdrop.multiplier.Multiplier;
import me.ivehydra.customdrops.utils.EnchantmentUtils;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.Random;

public class PlayerFishListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent e) {
        if(e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if(e.isCancelled()) return;

        Player p = e.getPlayer();
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropFishing customDropFishing = customDropManager.getFishingCustomDrops();

        if(!customDropFishing.isEnabled()) return;

        Entity caught = e.getCaught();
        if(customDropFishing.isVanillaDropsDisabled() && customDropFishing.areDropsConditionsTrue(p))
            Objects.requireNonNull(caught).remove();
        else
            if(customDropFishing.isAutoPickupEnabled())
                addItem(p, (ItemStack) Objects.requireNonNull(caught), true, e);

        if(customDropFishing.isVanillaEXPDisabled() && customDropFishing.areEXPConditionsTrue(p))
            e.setExpToDrop(0);

        for(CustomDrop customDrop : customDropFishing.getCustomDrops()) {

            if(!customDrop.areConditionsTrue(p)) continue;

            double chance = customDrop.getChance();
            Multiplier chanceMultiplier = customDrop.getChanceMultiplier();
            ItemStack itemStack;
            if(VersionUtils.isAtLeastVersion19()) itemStack = p.getInventory().getItemInMainHand();
            else itemStack = p.getItemInHand();

            if(!chanceMultiplier.isDisabled()) {
                int luckLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LUCK);
                double percentagePerLevel = chanceMultiplier.getValue();
                chance += chance * (luckLevel * percentagePerLevel);
            }

            Random random = new Random();

            if(random.nextDouble() < chance) {
                World world = p.getWorld();
                Location loc = p.getLocation();
                switch(customDrop.getType()) {
                    case ITEM:
                        ItemStack drop = customDrop.getItemStack();
                        if(customDrop.isAutoPickupEnabled())
                            addItem(p, drop, false, e);
                        else
                            world.dropItemNaturally(loc, drop);
                        break;
                    case ITEMS:
                        for(ItemStack drops : customDrop.getItemStacks()) {
                            if(customDrop.isAutoPickupEnabled())
                                addItem(p, drops, false, e);
                            else
                                world.dropItemNaturally(loc, drops);
                        }
                        break;
                }
                instance.getActionManager().execute(p, customDrop.getActions());

                for(CustomDropEXP customDropEXP : customDrop.getCustomDropEXPs()) {

                    if(!customDropEXP.areConditionsTrue(p)) continue;

                    double expChance = customDropEXP.getChance();
                    Multiplier expChanceMultiplier = customDropEXP.getChanceMultiplier();

                    if(!expChanceMultiplier.isDisabled()) {
                        int luckLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LUCK);
                        double percentagePerLevel = expChanceMultiplier.getValue();
                        expChance += expChance * (luckLevel * percentagePerLevel);
                    }

                    int exp = customDropEXP.getEXP();
                    Multiplier expMultiplier = customDropEXP.getEXPMultiplier();

                    if(!expMultiplier.isDisabled()) {
                        int luckLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LUCK);
                        double percentagePerLevel = expMultiplier.getValue();
                        exp += (int) (exp * (luckLevel * percentagePerLevel));
                    }

                    if(random.nextDouble() < expChance) {
                        p.giveExp(exp);
                        instance.getActionManager().execute(p, customDropEXP.getActions());
                    }

                }

            }

        }

    }

    private void addItem(Player p, ItemStack itemStack, boolean clear, PlayerFishEvent e) {
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
            World world = p.getWorld();
            Location loc = p.getLocation();
            clone.setAmount(add);
            world.dropItemNaturally(loc, clone);
            p.sendMessage(MessageUtils.INVENTORY_FULL.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
        }

        if(clear)
            Objects.requireNonNull(e.getCaught()).remove();

    }

}
