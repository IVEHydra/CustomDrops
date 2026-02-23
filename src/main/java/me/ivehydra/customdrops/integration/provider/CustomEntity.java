package me.ivehydra.customdrops.integration.provider;

import me.ivehydra.customdrops.integration.PluginIntegration;
import org.bukkit.entity.LivingEntity;

public interface CustomEntity extends PluginIntegration {

    boolean isCustomEntity(LivingEntity entity);

    String getEntityId(LivingEntity entity);

}
