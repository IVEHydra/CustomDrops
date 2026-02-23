package me.ivehydra.customdrops.integration.plugins;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.integration.provider.CustomEntity;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;

public class MythicMobsIntegration implements CustomEntity {

    private final CustomDrops instance = CustomDrops.getInstance();

    @Override
    public boolean isEnabled() { return instance.isPluginPresent("MythicMobs"); }

    @Override
    public void onEnable() { instance.sendLog("[CustomDrops]" + ChatColor.GREEN + " MythicMobs has been found. Now you can set Custom Drops for Custom Entities."); }

    @Override
    public boolean isCustomEntity(LivingEntity entity) {
        MythicMobs mythicInstance = MythicMobs.inst();
        return mythicInstance.getMobManager().isActiveMob(entity.getUniqueId());
    }

    @Override
    public String getEntityId(LivingEntity entity) {
        MythicMobs mythicInstance = MythicMobs.inst();
        return mythicInstance.getMobManager().getActiveMob(entity.getUniqueId()).map(ActiveMob::getMobType).orElse(null);
    }

}
