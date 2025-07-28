package me.ivehydra.customdrops.listeners;

import com.cryptomorin.xseries.XSound;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropEXP;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.CustomDropSettings;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropBlock;
import me.ivehydra.customdrops.customdrop.multiplier.ChanceMultiplier;
import me.ivehydra.customdrops.utils.EnchantmentUtils;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Random;

public class BlockBreakListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if(e.isCancelled()) return;

        Player p = e.getPlayer();
        Block block = e.getBlock();
        Material material = block.getType();
        CustomDropManager customDropManager = instance.getCustomDropManager();
        CustomDropBlock customDropBlock = customDropManager.getBlockCustomDrops().get(material.name());

        if(customDropBlock != null) {

            if(!customDropBlock.isEnabled()) return;

            ItemStack itemStack;
            if(VersionUtils.isAtLeastVersion19()) itemStack = p.getInventory().getItemInMainHand();
            else itemStack = p.getItemInHand();

            CustomDropSettings customDropSettings = customDropManager.getCustomDropSettings();
            if(customDropSettings.isEnabled() && customDropSettings.getWorlds().contains(p.getWorld()) && hasSilkTouch(itemStack)) return;

            if(customDropBlock.isVanillaDropsDisabled() && customDropBlock.areDropsConditionsTrue(p))
                clearVanillaDrops(e, block, itemStack, p);
            else
                for(ItemStack drops : block.getDrops()) {
                    if(customDropBlock.isAutoPickupEnabled())
                        addItem(p, drops, block, true, e, itemStack);
                }

            if(customDropBlock.isVanillaEXPDisabled() && customDropBlock.areEXPConditionsTrue(p))
                e.setExpToDrop(0);

            for(CustomDrop customDrop : customDropBlock.getCustomDrops()) {

                if(customDrop.isForNaturalDisabled() && !isPlacedBlock(block))
                    continue;
                if(customDrop.isForPlacedDisabled() && isPlacedBlock(block))
                    continue;

                if(!customDrop.areConditionsTrue(p)) continue;

                double chance = customDrop.getChance();
                ChanceMultiplier chanceMultiplier = customDrop.getChanceMultiplier();

                if(!chanceMultiplier.isDisabled()) {
                    int fortuneLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.FORTUNE);
                    double percentagePerLevel = chanceMultiplier.getValue();
                    chance += chance * (fortuneLevel * percentagePerLevel);
                }

                Random random = new Random();

                if(random.nextDouble() < chance) {
                    World world = block.getWorld();
                    Location loc = block.getLocation();
                    switch(customDrop.getType()) {
                        case ITEM:
                            ItemStack drop = customDrop.getItemStack();
                            if(customDrop.isAutoPickupEnabled())
                                addItem(p, drop, block, false, e, itemStack);
                            else
                                world.dropItemNaturally(loc, drop);
                            break;
                        case ITEMS:
                            for(ItemStack drops : customDrop.getItemStacks()) {
                                if(customDrop.isAutoPickupEnabled())
                                    addItem(p, drops, block, false, e, itemStack);
                                else
                                    world.dropItemNaturally(loc, drops);
                            }
                            break;
                    }

                    instance.getActionManager().execute(p, customDrop.getActions());

                    for(CustomDropEXP customDropEXP : customDrop.getCustomDropEXPs()) {

                        if(customDropEXP.isForNaturalDisabled() && !isPlacedBlock(block))
                            continue;
                        if(customDropEXP.isForPlacedDisabled() && isPlacedBlock(block))
                            continue;

                        if(!customDropEXP.areConditionsTrue(p)) continue;

                        double expChance = customDropEXP.getChance();
                        ChanceMultiplier expChanceChanceMultiplier = customDropEXP.getChanceMultiplier();

                        if(!expChanceChanceMultiplier.isDisabled()) {
                            int fortuneLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.FORTUNE);
                            double percentagePerLevel = expChanceChanceMultiplier.getValue();
                            expChance += expChance * (fortuneLevel * percentagePerLevel);
                        }

                        int exp = customDropEXP.getEXP();
                        ChanceMultiplier expMultiplier = customDropEXP.getEXPMultiplier();

                        if(!expMultiplier.isDisabled()) {
                            int fortuneLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.FORTUNE);
                            double percentagePerLevel = expMultiplier.getValue();
                            exp += (int) (exp * (fortuneLevel * percentagePerLevel));
                        }

                        if(random.nextDouble() < expChance) {
                            p.giveExp(exp);
                            instance.getActionManager().execute(p, customDropEXP.getActions());
                        }

                    }

                }

            }

            if(isPlacedBlock(block)) block.removeMetadata("CustomDrops_Block_Placed", instance);

        }
    }

    private boolean isPlacedBlock(Block block) {
        List<MetadataValue> metadataValues = block.getMetadata("CustomDrops_Block_Placed");
        for(MetadataValue value : metadataValues) return value.asBoolean();
        return false;
    }

    private boolean hasSilkTouch(ItemStack itemStack) {
        int silkTouch = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.SILK_TOUCH);
        return silkTouch != 0;
    }

    private void addItem(Player p, ItemStack itemStack, Block block, boolean clear, BlockBreakEvent e, ItemStack hand) {
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
            World world = block.getWorld();
            Location loc = block.getLocation();
            clone.setAmount(add);
            world.dropItemNaturally(loc, clone);
            p.sendMessage(MessageUtils.INVENTORY_FULL.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
        }

        if(clear)
            clearVanillaDrops(e, block, hand, p);

    }

    private void clearVanillaDrops(BlockBreakEvent e, Block block, ItemStack itemStack, Player p) {
        if(VersionUtils.isAtLeastVersion114())
            e.setDropItems(false);
        else {
            e.setCancelled(true);
            block.setType(Material.AIR);
            Material itemMaterial = itemStack.getType();
            if(p.getGameMode() != GameMode.CREATIVE && itemMaterial != Material.AIR && !NBTEditor.getBoolean(itemStack, "Unbreakable")) {
                int durability = 0;
                if(VersionUtils.isAtLeastVersion113()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(itemMeta instanceof Damageable) {
                        Damageable damageable = (Damageable) itemMeta;
                        durability = damageable.getDamage();
                        damageable.setDamage(durability + 1);
                        itemStack.setItemMeta(itemMeta);
                    }
                } else {
                    durability = itemStack.getDurability();
                    itemStack.setDurability((short) (durability + 1));
                }
                if(durability >= itemMaterial.getMaxDurability()) {
                    p.getInventory().remove(itemStack);
                    XSound.BLOCK_ANVIL_BREAK.play(p.getLocation());
                }
            }
        }
    }

}
