package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.CustomDropSettings;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropFishing;
import me.ivehydra.customdrops.customdrop.multiplier.MultiplierType;
import me.ivehydra.customdrops.utils.EnchantmentUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

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
        CustomDropFishing customDropFishing = customDropManager.getFishingCustomDrop();

        if(!customDropFishing.isEnabled()) return;

        if(customDropFishing.isVanillaDropsDisabled() && customDropFishing.getVanillaDropsWorlds().contains(p.getWorld()))
            Objects.requireNonNull(e.getCaught()).remove();

        if(customDropFishing.isVanillaEXPDisabled() && customDropFishing.getVanillaEXPWorlds().contains(p.getWorld()))
            e.setExpToDrop(0);

        Random random = new Random();

        for(CustomDrop customDrop : customDropFishing.getCustomDrops()) {

            if(!customDrop.areConditionsTrue(p)) continue;

            double chance = customDrop.getChance();
            CustomDropSettings settings = customDropManager.getSettings();

            if(settings.isEnabled(MultiplierType.LUCK) && settings.getWorlds(MultiplierType.LUCK).contains(p.getWorld())) {
                ItemStack itemStack;
                if(VersionUtils.isAtLeastVersion19()) itemStack = p.getInventory().getItemInMainHand();
                else itemStack = p.getItemInHand();
                int luckOfTheSeaLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.LUCK);
                double percentagePerLevel = settings.getLevel(MultiplierType.LUCK);
                chance += chance * (luckOfTheSeaLevel * percentagePerLevel);
            }

            if(random.nextDouble() < chance) {
                switch(customDrop.getType()) {
                    case ITEM:
                        p.getWorld().dropItemNaturally(p.getLocation(), customDrop.getItemStack());
                        break;
                    case ITEMS:
                        for(ItemStack itemStack : customDrop.getItemStacks())
                            p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
                        break;
                }
                instance.getActionManager().execute(p, customDrop.getActions());
                p.giveExp(customDrop.getExp());
            }

        }

    }

}
