package me.ivehydra.customdrops.action;

import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.action.actions.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class ActionManager {

    private final CustomDrops instance = CustomDrops.getInstance();
    private final Map<String, Action> actions;

    public ActionManager() {
        actions = new HashMap<>();

        load();
    }

    private void load() {
        register(
                new SoundAction(),
                new MessageAction(),
                new JSONMessageAction(),
                new TitleAction(),
                new ActionBarAction(),
                new CommandConsoleAction(),
                new CommandPlayerAction()
        );
    }

    private void register(Action... actions) { Arrays.asList(actions).forEach(action -> this.actions.put(action.getName(), action)); }

    public void execute(Player p, List<String> actions) {
        actions.forEach(string -> {
            String name = StringUtils.substringBetween(string, "[", "]");
            Action action = name == null ? null : this.actions.get(name.toUpperCase());

            if(action != null) {
                string = string.split(" ", 2)[1];
                string = me.ivehydra.customdrops.utils.StringUtils.getColoredString(string
                        .replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))
                        .replace("%player_name%", p.getName())
                );
                if(instance.isPlaceholderAPIPresent()) string = PlaceholderAPI.setPlaceholders(p, string);
                action.execute(p, string);
            }
        });
    }

}
