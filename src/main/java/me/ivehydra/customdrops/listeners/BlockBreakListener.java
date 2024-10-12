package me.ivehydra.customdrops.listeners;

import com.cryptomorin.xseries.XSound;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.customdrop.CustomDropSettings;
import me.ivehydra.customdrops.customdrop.handlers.CustomDropBlock;
import me.ivehydra.customdrops.customdrop.multiplier.MultiplierType;
import me.ivehydra.customdrops.utils.EnchantmentUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
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
            if(customDropBlock.isVanillaDropsDisabled() && customDropBlock.getVanillaDropsWorlds().contains(p.getWorld())) {
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

            if(customDropBlock.isVanillaEXPDisabled() && customDropBlock.getVanillaEXPWorlds().contains(p.getWorld()))
                e.setExpToDrop(0);

            Random random = new Random();

            for(CustomDrop customDrop : customDropBlock.getCustomDrops()) {

                if(customDrop.isForNaturalDisabled() && !isPlacedBlock(block))
                    continue;
                if(customDrop.isForPlacedDisabled() && isPlacedBlock(block))
                    continue;

                if(!customDrop.areConditionsTrue(p)) continue;

                double chance = customDrop.getChance();
                CustomDropSettings settings = customDropManager.getSettings();

                if(settings.isEnabled(MultiplierType.FORTUNE) && settings.getWorlds(MultiplierType.FORTUNE).contains(p.getWorld())) {
                    int fortuneLevel = EnchantmentUtils.getEnchantmentLevel(itemStack, EnchantmentUtils.FORTUNE);
                    double percentagePerLevel = settings.getLevel(MultiplierType.FORTUNE);
                    chance += chance * (fortuneLevel * percentagePerLevel);
                }

                if(random.nextDouble() < chance) {
                    switch(customDrop.getType()) {
                        case ITEM:
                            block.getWorld().dropItemNaturally(block.getLocation(), customDrop.getItemStack());
                            break;
                        case ITEMS:
                            for(ItemStack drop : customDrop.getItemStacks())
                                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                            break;
                    }
                    instance.getActionManager().execute(p, customDrop.getActions());
                    p.giveExp(customDrop.getExp());
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

}
