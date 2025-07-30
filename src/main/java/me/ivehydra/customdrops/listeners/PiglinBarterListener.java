package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropEXP;
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

            if(random.nextDouble() < chance) {
                World world = piglin.getWorld();
                switch(customDrop.getType()) {
                    case ITEM:
                        ItemStack drop = customDrop.getItemStack();
                        if(customDrop.isAutoPickupEnabled())
                            addItem(p, drop, piglin);
                        else
                            world.dropItemNaturally(loc, drop);
                        break;
                    case ITEMS:
                        for(ItemStack drops : customDrop.getItemStacks()) {
                            if(customDrop.isAutoPickupEnabled())
                                addItem(p, drops, piglin);
                            else
                                world.dropItemNaturally(loc, drops);
                        }
                        break;
                }

                instance.getActionManager().execute(p, customDrop.getActions());

                for(CustomDropEXP customDropEXP : customDrop.getCustomDropEXPs()) {

                    if(!customDropEXP.areConditionsTrue(p)) continue;

                    double expChance = customDropEXP.getChance();
                    int exp = customDropEXP.getEXP();

                    if(random.nextDouble() < expChance) {
                        p.giveExp(exp);
                        instance.getActionManager().execute(p, customDropEXP.getActions());
                    }

                }

            }

        }

    }

    private void addItem(Player p, ItemStack itemStack, Entity entity) {
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

    }

}
