package me.ivehydra.customdrops.customdrop;

import me.ivehydra.customdrops.customdrop.multiplier.MultiplierSettings;
import me.ivehydra.customdrops.customdrop.multiplier.MultiplierType;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDropSettings {

    private final Map<MultiplierType, MultiplierSettings> multipliers;

    public CustomDropSettings(boolean fortune, List<String> fortuneWorlds, double fortuneLevel, boolean looting, List<String> lootingWorlds, double lootingLevel, boolean luck, List<String> luckWorlds, double luckLevel) {
        multipliers = new HashMap<>();
        multipliers.put(MultiplierType.FORTUNE, new MultiplierSettings(fortune, fortuneWorlds, fortuneLevel));
        multipliers.put(MultiplierType.LOOTING, new MultiplierSettings(looting, lootingWorlds, lootingLevel));
        multipliers.put(MultiplierType.LUCK, new MultiplierSettings(luck, luckWorlds, luckLevel));
    }

    public boolean isEnabled(MultiplierType type) { return multipliers.get(type).isEnabled(); }

    public void setEnabled(MultiplierType type, boolean enabled) { multipliers.get(type).setEnabled(enabled); }

    public List<World> getWorlds(MultiplierType type) { return multipliers.get(type).getWorlds(); }

    public void addWorld(MultiplierType type, World world) { multipliers.get(type).addWorld(world); }

    public void removeWorld(MultiplierType type, World world) { multipliers.get(type).removeWorld(world); }

    public double getLevel(MultiplierType type) { return multipliers.get(type).getLevel(); }

    public void setLevel(MultiplierType type, double value) { multipliers.get(type).setLevel(value); }

}
