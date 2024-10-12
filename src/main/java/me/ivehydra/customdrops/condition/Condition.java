package me.ivehydra.customdrops.condition;

import org.bukkit.entity.Player;

public class Condition {

    private final String variable;
    private final ConditionType type;
    private final String value;

    public Condition(String condition) {
        String[] args = condition.split(" ");
        this.variable = args[0].trim();
        this.type = ConditionType.fromString(args[1].trim());
        this.value = args[2].trim();
    }

    public boolean evaluate(Player p) {
        String actualValue = Variable.getVariable(variable, p);
        return type.evaluate(actualValue, value);
    }

}
