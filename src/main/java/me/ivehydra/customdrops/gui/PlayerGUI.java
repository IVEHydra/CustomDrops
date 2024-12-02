package me.ivehydra.customdrops.gui;

import me.ivehydra.customdrops.customdrop.CustomDrop;
import org.bukkit.entity.Player;

public class PlayerGUI {

    private final Player p;
    private CustomDrop customDrop;
    private GUI gui;
    private int page;

    public PlayerGUI(Player p) { this.p = p; }

    public Player getPlayer() { return p; }

    public CustomDrop getCustomDrop() { return customDrop; }

    public void setCustomDrop(CustomDrop customDrop) { this.customDrop = customDrop; }

    public GUI getGUI() { return gui; }

    public void setGUI(GUI gui) { this.gui = gui; }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

}
