package me.ivehydra.customdrops.customdrop;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CustomDropHandler {

    protected boolean enabled;
    protected boolean disableVanillaDrops;
    protected List<World> vanillaDropsWorlds;
    protected boolean disableVanillaEXP;
    protected List<World> vanillaEXPWorlds;
    protected final List<CustomDrop> customDrops;

    public CustomDropHandler(boolean enabled, boolean disableVanillaDrops, List<String> vanillaDropsWorlds, boolean disableVanillaEXP, List<String> vanillaEXPWorlds, List<CustomDrop> customDrops) {
        this.enabled = enabled;
        this.disableVanillaDrops = disableVanillaDrops;
        this.vanillaDropsWorlds = loadWorlds(vanillaDropsWorlds);
        this.disableVanillaEXP = disableVanillaEXP;
        this.vanillaEXPWorlds = loadWorlds(vanillaEXPWorlds);
        this.customDrops = customDrops;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isVanillaDropsDisabled() { return disableVanillaDrops; }

    public void setVanillaDrops(boolean disableVanillaDrops) { this.disableVanillaDrops = disableVanillaDrops; }

    public List<World> getVanillaDropsWorlds() { return vanillaDropsWorlds; }

    public void addVanillaDropsWorld(World world) { if(!vanillaDropsWorlds.contains(world)) vanillaDropsWorlds.add(world); }

    public void removeVanillaDropsWorld(World world) { vanillaDropsWorlds.remove(world); }

    public boolean isVanillaEXPDisabled() { return disableVanillaEXP; }

    public void setVanillaEXP(boolean disableVanillaEXP) { this.disableVanillaEXP = disableVanillaEXP; }

    public List<World> getVanillaEXPWorlds() { return vanillaEXPWorlds; }

    public void addVanillaEXPWorld(World world) { if(!vanillaEXPWorlds.contains(world)) vanillaEXPWorlds.add(world); }

    public void removeVanillaEXPWorld(World world) { vanillaEXPWorlds.remove(world); }

    private List<World> loadWorlds(List<String> worlds) { return worlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()); }

    public List<CustomDrop> getCustomDrops() { return customDrops; }

}
