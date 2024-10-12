package me.ivehydra.customdrops.action.actions;

import com.cryptomorin.xseries.XSound;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.action.Action;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class SoundAction implements Action {

    private final CustomDrops instance = CustomDrops.getInstance();


    @Override
    public String getName() { return "SOUND"; }

    @Override
    public void execute(Player p, String string) {
        String[] args = string.split(";");
        double volume, pitch;

        try {
            volume = Integer.parseInt(args[1]);
            pitch = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            volume = 1.0;
            pitch = 1.0;
        }

        Optional<XSound> sound = XSound.matchXSound(args[0]);
        if(!sound.isPresent()) return;
        try {
            p.playSound(p.getLocation(), Objects.requireNonNull(sound.get().parseSound()), (float) volume, (float) pitch);
        } catch(Exception e) {
            instance.sendLog("[CustomDrops]" + ChatColor.RED + " An error occurred while trying to play the sound. Ensure that the sound name is correct and supported by the server.");
            instance.sendLog("[CustomDrops]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

}
