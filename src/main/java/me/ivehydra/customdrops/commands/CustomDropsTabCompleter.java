package me.ivehydra.customdrops.commands;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.file.FileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomDropsTabCompleter implements TabCompleter {

    private final CustomDrops instance = CustomDrops.getInstance();
    private final FileManager fileManager = instance.getFileManager();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(cmd.getName().equalsIgnoreCase("customdrops")) {
            List<String> argsList = new ArrayList<>();
            if(args.length == 1) {
                if(hasPermission(sender, "customdrops.editor")) argsList.add("edit");
                if(hasPermission(sender, "customdrops.help")) argsList.add("help");
                if(hasPermission(sender, "customdrops.reload")) argsList.add("reload");
                return argsList.stream().filter(string -> string.startsWith(args[0])).collect(Collectors.toList());
            }

            if(args[0].equalsIgnoreCase("edit")) {
                if(!hasPermission(sender, "customdrops.editor"))
                    return Collections.emptyList();
            }

            if(args.length == 2 && args[0].equalsIgnoreCase("edit")) {
                CustomDropManager customDropManager = instance.getCustomDropManager();
                argsList.addAll(customDropManager.getBlockNames());
                argsList.addAll(customDropManager.getEntityNames());
                argsList.add("FISHING");
                argsList.add("PIGLINBARTERING");
                return argsList.stream().filter(string -> string.startsWith(args[1])).collect(Collectors.toList());
            }

            if(args.length == 3 && args[0].equalsIgnoreCase("edit")) {
                CustomDropManager customDropManager = instance.getCustomDropManager();
                String name = args[1];

                if(customDropManager.getBlockNames().contains(name))
                    argsList.addAll(getNumbers("blocks", name));
                else if(customDropManager.getEntityNames().contains(name))
                    argsList.addAll(getNumbers("entities", name));
                else if(name.equalsIgnoreCase("fishing"))
                    argsList.addAll(getNumbers("fishing", name));
                else if(name.equalsIgnoreCase("piglinbartering"))
                    argsList.addAll(getNumbers("piglinbartering", name));

                return argsList.stream().filter(string -> string.startsWith(args[2])).collect(Collectors.toList());
            }
            return argsList;
        }
        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if(!(sender instanceof Player)) return true;
        else return sender.hasPermission(permission);
    }

    private List<String> getNumbers(String category, String name) {
        List<String> numbers = new ArrayList<>();
        YamlConfiguration config;
        ConfigurationSection section;
        switch(category) {
            case "blocks":
                config = fileManager.getFile("drops", "blocks.yml").getConfig();
                section = config.getConfigurationSection("customDrops.blocks." + name + ".drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
            case "entities":
                config = fileManager.getFile("drops", "entities.yml").getConfig();
                section = config.getConfigurationSection("customDrops.entities." + name + ".drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
            case "fishing":
                config = fileManager.getFile("drops", "fishing.yml").getConfig();
                section = config.getConfigurationSection("customDrops.fishing.drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
            case "piglinbartering":
                config = fileManager.getFile("drops", "piglinbartering.yml").getConfig();
                section = config.getConfigurationSection("customDrops.piglinbartering.drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
        }
        return numbers;
    }

}
