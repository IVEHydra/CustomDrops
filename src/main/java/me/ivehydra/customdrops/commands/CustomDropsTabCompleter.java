package me.ivehydra.customdrops.commands;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomDropsTabCompleter implements TabCompleter {

    private final CustomDrops instance = CustomDrops.getInstance();

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
            if(args.length == 2 && args[0].equalsIgnoreCase("edit")) {
                CustomDropManager customDropManager = instance.getCustomDropManager();
                argsList.addAll(customDropManager.getBlockNames());
                argsList.addAll(customDropManager.getEntityNames());
                argsList.add("FISHING");
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
        ConfigurationSection section;
        switch(category) {
            case "blocks":
                section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + name + ".drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
            case "entities":
                section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + name + ".drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
            case "fishing":
                section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops");
                if(section != null)
                    numbers.addAll(section.getKeys(false));
                break;
        }
        return numbers;
    }

}
