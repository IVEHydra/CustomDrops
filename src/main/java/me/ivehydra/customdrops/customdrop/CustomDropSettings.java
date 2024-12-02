package me.ivehydra.customdrops.customdrop;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomDropSettings {

    private boolean enabled;
    private List<World> worlds;

    public CustomDropSettings(boolean enabled, List<String> worlds) {
        this.enabled = enabled;
        this.worlds = loadWorlds(worlds);
    }

    public boolean isEnabled() { return enabled; }

    public List<World> getWorlds() { return worlds; }

    private List<World> loadWorlds(List<String> worlds) { return worlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()); }

}
