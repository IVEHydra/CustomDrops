package me.ivehydra.customdrops.gui;

import me.ivehydra.customdrops.CustomDrops;

public abstract class PaginatedGUI extends GUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    protected int page = 0;
    protected int maxItemsPerPage = 45;
    protected int index = 0;

    public PaginatedGUI(PlayerGUI playerGUI) { super(playerGUI); }

    public int getMaxItemsPerPage() { return maxItemsPerPage; }

    public void addItems() {
        inv.setItem(48, createItemStack(instance.getConfig().getString("gui.general.items.previousPage.name"), instance.getConfig().getStringList("gui.general.items.previousPage.lore"), instance.getConfig().getString("gui.general.items.previousPage.material")));
        inv.setItem(49, createItemStack(instance.getConfig().getString("gui.general.items.close.name"), instance.getConfig().getStringList("gui.general.items.close.lore"), instance.getConfig().getString("gui.general.items.close.material")));
        inv.setItem(50, createItemStack(instance.getConfig().getString("gui.general.items.nextPage.name"), instance.getConfig().getStringList("gui.general.items.nextPage.lore"), instance.getConfig().getString("gui.general.items.nextPage.material")));
    }

}
