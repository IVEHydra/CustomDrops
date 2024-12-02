package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        instance.removePlayerGUI(p);

    }

}
