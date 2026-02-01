package me.ivehydra.customdrops.file;

import me.ivehydra.customdrops.CustomDrops;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class CustomFile {

    private final CustomDrops instance =  CustomDrops.getInstance();
    private final File file;
    private final String folder;
    private YamlConfiguration config;

    public CustomFile(String folder, String fileName) {
        this.folder = folder;
        File dir = folder == null ? instance.getDataFolder() : new File(instance.getDataFolder(), folder);

        if(!dir.exists() && !dir.mkdirs())
            instance.sendLog(ChatColor.RED + "[CustomDrops]" + ChatColor.RED + " Could not create folder: " + dir.getPath());

        this.file = new File(dir, fileName);

        create();
    }

    private void create() {

        if(!file.exists()) {
            String name = file.getName();
            try {
                String path = folder == null ? name : folder + "/" + name;
                InputStream in = instance.getResource(path);

                if(in != null) {
                    Files.copy(in, file.toPath());
                    in.close();
                } else {
                    if(!file.createNewFile())
                        instance.sendLog(ChatColor.RED + "[CustomDrops]" + ChatColor.RED + " Could not create file: " + name);
                }
            } catch(IOException e) {
                instance.sendLog("[CustomDrops]" + ChatColor.RED + " Could not create file: " + name);
                instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error Details: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfig() { return config; }

    public void reload() { config = YamlConfiguration.loadConfiguration(file); }

    public void save() {
        try {
            config.save(file);
        } catch(IOException e) {
            instance.sendLog("[CustomDrops]" + ChatColor.RED + " Could not save file: " + file.getName());
            instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error Details: " + e.getMessage());
        }
    }

}
