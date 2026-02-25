package me.ivehydra.customdrops.integration;

import me.ivehydra.customdrops.integration.provider.CustomEntity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PluginsManager {

    private final List<CustomEntity> customEntities = new ArrayList<>();

    public void register(PluginIntegration integration) {
        if(!integration.isEnabled())
            return;

        integration.onEnable();

        if(integration instanceof CustomEntity)
            customEntities.add((CustomEntity)integration);
    }

    public String getEntityID(LivingEntity entity) {
        for(CustomEntity customEntity : customEntities) {
            if(customEntity.isCustomEntity(entity))
                return customEntity.getEntityId(entity);
        }
        return entity.getType().toString();
    }

}
