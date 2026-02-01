package me.ivehydra.customdrops.action;

import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.action.actions.*;
import me.ivehydra.customdrops.utils.MessageUtils;
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
                new CommandPlayerAction(),
                new WaitAction()
        );
    }

    private void register(Action... actions) { Arrays.asList(actions).forEach(action -> this.actions.put(action.getName(), action)); }

    public void execute(Player p, List<String> actions) { executeActions(p, actions, 0); }

    private void executeActions(Player p, List<String> actions, int index) {
        if(index >= actions.size())
            return;

        String string = actions.get(index);
        String name = StringUtils.substringBetween(string, "[", "]");
        Action action = getActionByName(name);

        if(action == null) {
            executeActions(p, actions, index + 1);
            return;
        }

        String args = string.contains(" ") ? string.split(" ", 2)[1] : "";
        args = me.ivehydra.customdrops.utils.StringUtils.getColoredString(args
                .replace("%prefix%", MessageUtils.PREFIX.toString())
        );

        if(instance.isPluginPresent("PlaceholderAPI"))
            args = PlaceholderAPI.setPlaceholders(p, args);

        action.execute(p, args, () -> executeActions(p, actions, index + 1));

    }

    private Action getActionByName(String name) { return actions.get(name.toUpperCase()); }

}
