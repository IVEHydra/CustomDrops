package me.ivehydra.customdrops.integration;

import me.ivehydra.customdrops.integration.provider.CustomBlock;
import me.ivehydra.customdrops.integration.provider.CustomEntity;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PluginsManager {

    private final List<CustomEntity> customEntities = new ArrayList<>();
    private final List<CustomBlock> customBlocks = new ArrayList<>();

    public void register(PluginIntegration integration) {
        if(!integration.isEnabled())
            return;

        integration.onEnable();

        if(integration instanceof CustomEntity)
            customEntities.add((CustomEntity)integration);
        else if(integration instanceof CustomBlock)
            customBlocks.add((CustomBlock)integration);
    }

    public String getEntityID(LivingEntity entity) {
        for(CustomEntity customEntity : customEntities) {
            if(customEntity.isCustomEntity(entity))
                return customEntity.getEntityId(entity);
        }
        return entity.getType().toString();
    }

    public String getBlockID(Block b) {
        for(CustomBlock customBlock : customBlocks) {
            if(customBlock.isCustomBlock(b))
                return customBlock.getBlockId(b);
        }
        return b.getType().name();
    }

}
