package me.ivehydra.customdrops.condition;

import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.entity.Player;

public class Variable {

    private static final CustomDrops instance = CustomDrops.getInstance();

    public static String getVariable(String variable, Player p) {
        if(instance.isPlaceholderAPIPresent()) {
            String resolved = PlaceholderAPI.setPlaceholders(p, variable);
            if(!resolved.equals(variable)) return resolved;
        }

        switch(variable) {
            case "%player_world%":
                return p.getWorld().getName();
            case "%player_gamemode%":
                return p.getGameMode().name();
            default:
                throw new IllegalArgumentException("[CustomDrops] Unknown Variable: " + variable);
        }
    }

    public static boolean isValidPlaceholder(String variable, Player p) {
        try {
            getVariable(variable, p);
            return true;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }

}
