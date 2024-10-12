package me.ivehydra.customdrops.condition;

import org.bukkit.entity.Player;

import java.util.List;

public class ConditionHandler {

    public boolean handle(Player p, List<String> conditions) {
        for(String conditionString : conditions) {
            Condition condition = new Condition(conditionString);
            if(!condition.evaluate(p)) return false;
        }
        return true;
    }

}
