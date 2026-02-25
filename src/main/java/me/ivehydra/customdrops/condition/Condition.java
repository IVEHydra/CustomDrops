package me.ivehydra.customdrops.condition;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition {

    private static final Pattern pattern = Pattern.compile(
            "(.+?)\\s+(==|!=|>=|<=|>|<|equals|!equals|equalsIgnoreCase|!equalsIgnoreCase|contains|!contains|startsWith|!startsWith|endsWith|!endsWith)\\s+(.+)"
    );
    private String variable;
    private ConditionType type;
    private String value;

    public Condition(String condition) {
        if(condition == null || condition.trim().isEmpty()) return;

        Matcher matcher = pattern.matcher(condition.trim());

        if(!matcher.matches())
            throw new IllegalArgumentException("[CustomDrops]" + ChatColor.RED + " Invalid Condition: " + condition);

        this.variable = matcher.group(1).trim();
        this.type = ConditionType.fromString(matcher.group(2).trim());
        this.value = matcher.group(3).trim();
    }

    public boolean evaluate(Player p) {
        if(variable == null && type == null && value == null) return true;
        String actualValue = Variable.getVariable(variable, p);
        return type.evaluate(actualValue, value);
    }

}
