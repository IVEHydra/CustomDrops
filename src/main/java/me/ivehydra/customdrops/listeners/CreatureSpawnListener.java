package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        String name = entity.getName();
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();

        if(isMythic(entity) || instance.getCustomDropManager().getEntityNames().contains(name)) {
            if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.DEFAULT) || spawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL)) instance.getNaturalEntities().add(entity.getUniqueId());
            if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) instance.getSpawnerEntities().add(entity.getUniqueId());
            if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) instance.getSpawnerEggEntities().add(entity.getUniqueId());
        }

    }

    private boolean isMythic(Entity entity) { return instance.getMythicEntities().containsKey(entity.getUniqueId()); }

}
