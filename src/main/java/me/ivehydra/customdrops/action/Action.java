package me.ivehydra.customdrops.action;

import org.bukkit.entity.Player;

public interface Action {

    String getName();

    void execute(Player p, String string);

}
