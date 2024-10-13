package me.ivehydra.customdrops.gui.guis;

import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class ActionsEditGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private boolean isBlock, isFishing;

    public ActionsEditGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
        this.isBlock = playerGUI.isBlock();
        this.isFishing = playerGUI.isFishing();
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.actionsEdit.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDrop customDrop = playerGUI.getCustomDrop();
        List<String> actions = customDrop.getActions();

        if(!actions.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= actions.size()) break;
                String action = actions.get(index);
                if(!action.isEmpty())
                    inv.addItem(createItemStack(action, instance.getConfig().getStringList("gui.actionsEdit.items.action.lore"), instance.getConfig().getString("gui.actionsEdit.items.action.material")));
            }
        }

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.actionsEdit.items.add.name"), instance.getConfig().getStringList("gui.actionsEdit.items.add.lore"), instance.getConfig().getString("gui.actionsEdit.items.add.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;

        int slot = e.getRawSlot();
        CustomDrop customDrop = playerGUI.getCustomDrop();
        List<String> actions = customDrop.getActions();

        switch(slot) {
            case 48:
                if(page == 0) p.sendMessage(MessageUtils.FIRST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                else {
                    page = page - 1;
                    playerGUI.setPage(playerGUI.getPage() - 1);
                    super.open();
                }
                break;
            case 49:
                new CustomDropEditGUI(playerGUI, playerGUI.getCustomDrop()).open();
                break;
            case 50:
                if(!((index + 1) >= actions.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(MessageUtils.LAST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                break;
            case 52:
                if(instance.containsWaitingCPlayer(p)) instance.removeWaitingCPlayer(p);
                instance.addWaitingCPlayer(p, false);
                String[] args = Objects.requireNonNull(instance.getConfig().getString("messages.action.insert")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix"))).split(";");
                Titles.sendTitle(p, 1, 999999999, 1, StringUtils.getColoredString(args[0]), StringUtils.getColoredString(args[1]));
                instance.getConfig().getStringList("messages.action.help").forEach(message -> p.sendMessage(StringUtils.getColoredString(message.replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix"))))));
                p.closeInventory();
                break;
            default:
                if(slot >= 0  && slot < actions.size())
                    customDrop.removeAction(slot);
                super.open();
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        ConfigurationSection section;
        CustomDrop customDrop = playerGUI.getCustomDrop();
        if(isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString() + ".drops." + customDrop.getNumber() + ".drop");
        else if(!isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString() + ".drops." + customDrop.getNumber() + ".drop");
        else section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops." + customDrop.getNumber() + ".drop");

        if(section != null) {
            List<String> actions = customDrop.getActions();
            section.set("actions", actions);
            instance.saveCustomDropsFile();
            instance.reloadCustomDropManager();
        }
    }

}
