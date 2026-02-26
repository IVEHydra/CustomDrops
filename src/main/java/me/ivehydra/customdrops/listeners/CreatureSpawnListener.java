package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

public class CreatureSpawnListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            String id = instance.getPluginsManager().getEntityID(entity);

            if(instance.getCustomDropManager().getEntityNames().contains(id)) {
                UUID uuid = entity.getUniqueId();

                if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.DEFAULT) || spawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL)) instance.getNaturalEntities().add(uuid);
                if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) instance.getSpawnerEntities().add(uuid);
                if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) instance.getSpawnerEggEntities().add(uuid);
                if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) instance.getCustomEntities().add(uuid);

            }
        }, 1L);
    }

}
