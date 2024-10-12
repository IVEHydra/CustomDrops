package me.ivehydra.customdrops.customdrop.multiplier;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MultiplierSettings {

    private boolean enabled;
    private List<World> worlds;
    private double level;

    public MultiplierSettings(boolean enabled, List<String> worlds, double level) {
        this.enabled = enabled;
        this.worlds = loadWorlds(worlds);
        setLevel(level);
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<World> getWorlds() { return worlds; }

    public void addWorld(World world) {
        if(!worlds.contains(world))
            worlds.add(world);
    }

    public void removeWorld(World world) { worlds.remove(world); }

    private List<World> loadWorlds(List<String> worlds) { return worlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()); }

    public double getLevel() { return level; }

    public void setLevel(double value) {
        level += value;
        level = Math.min(Math.max(level, 0), 100);
    }

}
