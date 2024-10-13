package me.ivehydra.customdrops.utils;

import me.ivehydra.customdrops.CustomDrops;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class VersionUtils {

    private static final CustomDrops instance = CustomDrops.getInstance();

    private static boolean isVersionAtLeast(int major, int min) {
        String version = Bukkit.getBukkitVersion();
        String numericVersion = version.split("-")[0];
        String[] args = numericVersion.split("\\.");

        try {
            int serverMajor = Integer.parseInt(args[0]);
            int serverMin = Integer.parseInt(args[1]);

            if(serverMajor > major) return true;
            if(serverMajor == major) return serverMin >= min;
            return false;
        } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error while parsing Bukkit Version: " + version);
            instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
            return false;
        }
    }

    public static boolean isAtLeastVersion19() { return isVersionAtLeast(1, 9); }

    public static boolean isAtLeastVersion113() { return isVersionAtLeast(1, 13); }

    public static boolean isAtLeastVersion114() { return isVersionAtLeast(1, 14); }

}
