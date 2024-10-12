package me.ivehydra.customdrops.utils;

import me.ivehydra.customdrops.CustomDrops;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static CustomDrops instance = CustomDrops.getInstance();
    private static Method method;
    private static boolean supportsHEX;

    static {
        try {
            method = ChatColor.class.getDeclaredMethod("of", String.class);
            supportsHEX = true;
        } catch(NoSuchMethodException e) {
            supportsHEX = false;
        }
    }

    public static String getColoredString(String string) {
        if(supportsHEX) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(string);

            while(matcher.find()) {
                String color = string.substring(matcher.start(), matcher.end());
                try {
                    string = string.replace(color, method.invoke(null, color).toString());
                } catch(Exception e) {
                    instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error converting HEX Color: " + color);
                    instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error Details: " + e.getMessage());
                }
                matcher = pattern.matcher(string);
            }
        }
        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

}
