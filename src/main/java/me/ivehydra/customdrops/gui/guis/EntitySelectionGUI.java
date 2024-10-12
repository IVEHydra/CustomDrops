package me.ivehydra.customdrops.gui.guis;

import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class EntitySelectionGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();

    public EntitySelectionGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.entitySelection.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDropManager customDropManager = instance.getCustomDropManager();
        List<String> entityNames = customDropManager.getEntityNames();

        if(!entityNames.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= entityNames.size()) break;
                String entity = entityNames.get(index);
                if(!entity.isEmpty())
                    inv.addItem(createItemStack(entity, instance.getConfig().getStringList("gui.entitySelection.items.entity.lore"), "SPAWNER"));
            }
        }

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.entitySelection.items.add.name"), instance.getConfig().getStringList("gui.entitySelection.items.add.lore"), instance.getConfig().getString("gui.entitySelection.items.add.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;

        CustomDropManager customDropManager = instance.getCustomDropManager();
        List<String> entityNames = customDropManager.getEntityNames();

        switch(e.getRawSlot()) {
            case 48:
                if(page == 0) p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.gui.firstPage")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
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
                if(!((index + 1) >= entityNames.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.gui.lastPage")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
                break;
            case 52:
                if(instance.containsWaitingPlayer(p)) instance.removeWaitingPlayer(p);
                instance.addWaitingPlayer(p, false);
                String[] args = Objects.requireNonNull(instance.getConfig().getString("messages.entity.insertName")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix"))).split(";");
                Titles.sendTitle(p, 1, 999999999, 1, StringUtils.getColoredString(args[0]), StringUtils.getColoredString(args[1]));
                p.closeInventory();
                break;
            default:
                String entity = ChatColor.stripColor(itemMeta.getDisplayName());
                switch(e.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        new EditGUI(playerGUI, entity, false, false).open();
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        instance.getCustomDropsFile().set("customDrops.entities." + entity, null);
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
