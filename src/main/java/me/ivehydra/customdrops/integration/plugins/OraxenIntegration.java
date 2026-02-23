package me.ivehydra.customdrops.integration.plugins;

import io.th0rgal.oraxen.api.OraxenBlocks;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.integration.provider.CustomBlock;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;

public class OraxenIntegration implements CustomBlock {

    private final CustomDrops instance = CustomDrops.getInstance();

    @Override
    public boolean isEnabled() { return instance.isPluginPresent("Oraxen"); }

    @Override
    public void onEnable() { instance.sendLog("[CustomDrops]" + ChatColor.GREEN + " Oraxen has been found. Now you can set Custom Drops for Custom Blocks."); }

    @Override
    public boolean isCustomBlock(Block block) { return OraxenBlocks.isOraxenBlock(block); }

    @Override
    public String getBlockId(Block block) {
        for(String id : OraxenBlocks.getBlockIDs()) {
            if(block.getType().name().equalsIgnoreCase(id))
                return id;
        }
        return null;
    }

}
