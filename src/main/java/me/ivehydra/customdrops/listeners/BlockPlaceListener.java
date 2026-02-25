package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockPlaceListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Block block = e.getBlock();
        String name = block.getType().name();

        if(instance.getCustomDropManager().getBlockNames().contains(name))
            block.setMetadata("CustomDrops_Block_Placed", new FixedMetadataValue(instance, block));

    }

}
