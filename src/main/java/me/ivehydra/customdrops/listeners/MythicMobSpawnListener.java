package me.ivehydra.customdrops.listeners;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class MythicMobSpawnListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onMythicMobSpawnEvent(MythicMobSpawnEvent e) {
        String mobType = e.getMob().getMobType();
        UUID uuid = e.getEntity().getUniqueId();

        if(instance.getCustomDropManager().getEntityNames().contains(mobType))
            instance.getMythicEntities().put(uuid, mobType);
    }

}
