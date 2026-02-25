package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropPiglinBartering;
import me.ivehydra.customdrops.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class PiglinBarterListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onPiglinBarterEvent(PiglinBarterEvent e) {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropPiglinBartering bartering = customDropManager.getBarteringCustomDrops();

        if(!bartering.isEnabled()) return;

        Location loc = e.getEntity().getLocation();
        double radius = 5.0;
        Optional<Player> player = e.getEntity().getWorld().getNearbyEntities(loc, radius, radius, radius, Player.class::isInstance).stream().map(Player.class::cast).min((p1, p2) -> (int)(p1.getLocation().distanceSquared(loc) - p2.getLocation().distanceSquared(loc)));

        if(!player.isPresent()) return;

        Player p = player.get();
        Piglin piglin = e.getEntity();
        List<ItemStack> outcome = e.getOutcome();

        if(bartering.isVanillaDropsDisabled() && bartering.areDropsConditionsTrue(p))
            outcome.clear();
        else {
            if(bartering.isAutoPickupEnabled()) {
                List<ItemStack> copies = new ArrayList<>(outcome);
                outcome.clear();
                for(ItemStack drops : copies)
                    addItem(p, drops, piglin);
            }
        }

        for(CustomDrop customDrop : bartering.getCustomDrops()) {

            if(!customDrop.areConditionsTrue(p)) continue;

            double chance = customDrop.getChance();
            Random random = new Random();
            boolean full = false;

            if(random.nextDouble() < chance) {
                World world = piglin.getWorld();
                switch(customDrop.getType()) {
                    case ITEM:
                        ItemStack drop = customDrop.getItemStack();
                        if(customDrop.isAutoPickupEnabled()) {
                            if(addItem(p, drop, piglin))
                                full = true;
                        } else
                            world.dropItemNaturally(loc, drop);
                        break;
                    case ITEMS:
                        for(ItemStack drops : customDrop.getItemStacks()) {
                            if(customDrop.isAutoPickupEnabled()) {
                                if(addItem(p, drops, piglin))
                                    full = true;
                            } else
                                world.dropItemNaturally(loc, drops);
                        }
                        break;
                }

                if(full)
                    p.sendMessage(MessageUtils.INVENTORY_FULL.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));

                instance.getActionManager().execute(p, customDrop.getActions());
                p.giveExp(customDrop.getEXP());

            }

        }

    }

    private boolean addItem(Player p, ItemStack itemStack, Entity entity) {
        PlayerInventory inv = p.getInventory();
        int add = itemStack.getAmount();
        ItemStack clone = itemStack.clone();

        for(int i = 0; i < 36 && add > 0; i++) {
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

        for(int i = 0; i < 36 && add > 0; i++) {
            ItemStack slot = inv.getItem(i);
            if(slot == null) {
                clone.setAmount(Math.min(add, clone.getMaxStackSize()));
                inv.setItem(i, clone.clone());
                add -= clone.getMaxStackSize();
            }
        }

        if(add > 0) {
            World world = entity.getWorld();
            Location loc = entity.getLocation();

            while(add > 0) {
                int dropAmount = Math.min(add, clone.getMaxStackSize());
                clone.setAmount(dropAmount);
                world.dropItemNaturally(loc, clone);
                add -= dropAmount;
            }

            return true;
        }

        return false;
    }

}
