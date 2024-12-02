package me.ivehydra.customdrops.listeners;

import com.cryptomorin.xseries.XSound;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropEXP;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.CustomDropSettings;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropBlock;
import me.ivehydra.customdrops.customdrop.multiplier.Multiplier;
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
            else {
                for(ItemStack drop : block.getDrops()) {
                    if(customDropBlock.isAutoPickupEnabled())
                        if(addItem(p, drop))
                            clearVanillaDrops(e, block, itemStack, p);
                }
            }

            if(customDropBlock.isVanillaEXPDisabled() && customDropBlock.areEXPConditionsTrue(p))
                e.setExpToDrop(0);

            Random random = new Random();

            for(CustomDrop customDrop : customDropBlock.getCustomDrops()) {

                if(customDrop.isForNaturalDisabled() && !isPlacedBlock(block))
                    continue;
                if(customDrop.isForPlacedDisabled() && isPlacedBlock(block))
                    continue;

                if(!customDrop.areConditionsTrue(p)) continue;

                double chance = customDrop.getChance();
                Multiplier chanceMultiplier = customDrop.getChanceMultiplier();

                if(!chanceMultiplier.isDisabled()) {
                    int fortuneLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.FORTUNE);
                    double percentagePerLevel = chanceMultiplier.getValue();
                    chance += chance * (fortuneLevel * percentagePerLevel);
                }

                if(random.nextDouble() < chance) {
                    World world = block.getWorld();
                    Location loc = block.getLocation();
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

                        if(customDropEXP.isForNaturalDisabled() && !isPlacedBlock(block))
                            continue;
                        if(customDropEXP.isForPlacedDisabled() && isPlacedBlock(block))
                            continue;

                        if(!customDropEXP.areConditionsTrue(p)) continue;

                        double expChance = customDropEXP.getChance();
                        Multiplier expMultiplier = customDropEXP.getEXPMultiplier();

                        if(!expMultiplier.isDisabled()) {
                            int fortuneLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.FORTUNE);
                            double percentagePerLevel = expMultiplier.getValue();
                            expChance += expChance * (fortuneLevel * percentagePerLevel);
                        }

                        if(random.nextDouble() < expChance) {
                            p.giveExp(customDropEXP.getEXP());
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

    private boolean addItem(Player p, ItemStack itemStack) {
        PlayerInventory inv = p.getInventory();
        if(inv.firstEmpty() == -1) {
            p.sendMessage(MessageUtils.INVENTORY_FULL.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            return false;
        }
        inv.addItem(itemStack);
        return true;
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
