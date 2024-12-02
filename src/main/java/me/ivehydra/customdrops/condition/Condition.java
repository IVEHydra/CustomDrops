package me.ivehydra.customdrops.condition;

import org.bukkit.entity.Player;

public class Condition {

    private String variable;
    private ConditionType type;
    private String value;

    public Condition(String condition) {
        if(condition == null || condition.trim().isEmpty()) return;

        String[] args = condition.split(" ");
        this.variable = args[0].trim();
        this.type = ConditionType.fromString(args[1].trim());
        this.value = args[2].trim();
    }

    public boolean evaluate(Player p) {
        if(variable == null && type == null && value == null) return true;
        String actualValue = Variable.getVariable(variable, p);
        return type.evaluate(actualValue, value);
    }

}
