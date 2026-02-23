package me.ivehydra.customdrops.integration.provider;

import me.ivehydra.customdrops.integration.PluginIntegration;
import org.bukkit.block.Block;

public interface CustomBlock extends PluginIntegration {

    boolean isCustomBlock(Block block);

    String getBlockId(Block block);

}
