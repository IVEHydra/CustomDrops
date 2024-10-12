package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();

        if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.DEFAULT) || spawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL)) instance.getNaturalEntities().add(entity.getUniqueId());
        if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) instance.getSpawnerEntities().add(entity.getUniqueId());
        if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) instance.getSpawnerEggEntities().add(entity.getUniqueId());

    }

}
