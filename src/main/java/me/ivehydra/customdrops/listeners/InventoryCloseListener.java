package me.ivehydra.customdrops.listeners;

import me.ivehydra.customdrops.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        if(holder instanceof GUI) {
            GUI gui = (GUI) holder;

            gui.handleInventoryCloseEvent(e);
        }
    }

}
