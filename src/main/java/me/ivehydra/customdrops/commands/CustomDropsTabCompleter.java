package me.ivehydra.customdrops.commands;

import me.ivehydra.customdrops.CustomDrops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
                if(hasPermission(sender, "customdrops.help")) argsList.add("help");
                if(hasPermission(sender, "customdrops.reload")) argsList.add("reload");
                return argsList.stream().filter(string -> string.startsWith(args[0])).collect(Collectors.toList());
            }
            return argsList;
        }
        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if(!(sender instanceof Player)) return true;
        else return sender.hasPermission(permission);
    }

}
