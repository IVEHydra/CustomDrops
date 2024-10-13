package me.ivehydra.customdrops.gui.guis;

import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class BlockSelectionGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();

    public BlockSelectionGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.blockSelection.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        List<String> blockNames = customDropManager.getBlockNames();

        if(!blockNames.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= blockNames.size()) break;
                String block = blockNames.get(index);
                if(!block.isEmpty())
                    inv.addItem(createItemStack("", instance.getConfig().getStringList("gui.blockSelection.items.block.lore"), block));
            }
        }

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.blockSelection.items.add.name"), instance.getConfig().getStringList("gui.blockSelection.items.add.lore"), instance.getConfig().getString("gui.blockSelection.items.add.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        List<String> blockNames = customDropManager.getBlockNames();

        switch(e.getRawSlot()) {
            case 48:
                if(page == 0) p.sendMessage(MessageUtils.FIRST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                else {
                    page = page - 1;
                    playerGUI.setPage(playerGUI.getPage() - 1);
                    super.open();
                }
                break;
            case 49:
                new MainGUI(playerGUI).open();
                break;
            case 50:
                if(!((index + 1) >= blockNames.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(MessageUtils.LAST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                break;
            case 52:
                if(instance.containsWaitingPlayer(p)) instance.removeWaitingPlayer(p);
                instance.addWaitingPlayer(p, true);
                String[] args = Objects.requireNonNull(instance.getConfig().getString("messages.block.insertName")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix"))).split(";");
                Titles.sendTitle(p, 1, 999999999, 1, StringUtils.getColoredString(args[0]), StringUtils.getColoredString(args[1]));
                p.closeInventory();
                break;
            default:
                String block = itemStack.getType().name().toUpperCase();
                switch(e.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        new EditGUI(playerGUI, block, true, false).open();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        instance.getCustomDropsFile().set("customDrops.blocks." + block, null);
                        instance.saveCustomDropsFile();
                        instance.reloadCustomDropManager();
                        super.open();
                        break;
                }
                break;
        }

    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) { }

}
