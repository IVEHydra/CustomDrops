package me.ivehydra.customdrops.gui.guis;

import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.gui.PaginatedGUI;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class ConditionsEditGUI extends PaginatedGUI {

    private final CustomDrops instance = CustomDrops.getInstance();
    private boolean isBlock, isFishing;

    public ConditionsEditGUI(PlayerGUI playerGUI) {
        super(playerGUI);
        playerGUI.setGUI(this);
        this.isBlock = playerGUI.isBlock();
        this.isFishing = playerGUI.isFishing();
    }

    @Override
    public String getName() { return StringUtils.getColoredString(instance.getConfig().getString("gui.conditionsEdit.title")); }

    @Override
    public int getSlots() { return 54; }

    @Override
    public void setItems() {
        CustomDrop customDrop = playerGUI.getCustomDrop();
        List<String> conditions = customDrop.getConditions();

        if(!conditions.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= conditions.size()) break;
                String condition = conditions.get(index);
                if(!condition.isEmpty())
                    inv.addItem(createItemStack(condition, instance.getConfig().getStringList("gui.conditionsEdit.items.condition.lore"), instance.getConfig().getString("gui.conditionsEdit.items.condition.material")));
            }
        }

        addItems();

        inv.setItem(52, createItemStack(instance.getConfig().getString("gui.conditionsEdit.items.add.name"), instance.getConfig().getStringList("gui.conditionsEdit.items.add.lore"), instance.getConfig().getString("gui.conditionsEdit.items.add.material")));

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR || Objects.equals(e.getClickedInventory(), p.getInventory())) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;

        CustomDrop customDrop = playerGUI.getCustomDrop();
        List<String> conditions = customDrop.getConditions();

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
                new CustomDropEditGUI(playerGUI, playerGUI.getCustomDrop()).open();
                break;
            case 50:
                if(!((index + 1) >= conditions.size())) {
                    page = page + 1;
                    playerGUI.setPage(playerGUI.getPage() + 1);
                    super.open();
                } else p.sendMessage(MessageUtils.LAST_PAGE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                break;
            case 52:
                if(instance.containsWaitingCPlayer(p)) instance.removeWaitingCPlayer(p);
                instance.addWaitingCPlayer(p, true);
                String[] args = Objects.requireNonNull(instance.getConfig().getString("messages.condition.insert")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix"))).split(";");
                Titles.sendTitle(p, 1, 999999999, 1, StringUtils.getColoredString(args[0]), StringUtils.getColoredString(args[1]));
                instance.getConfig().getStringList("messages.condition.help").forEach(message -> p.sendMessage(StringUtils.getColoredString(message.replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix"))))));
                p.closeInventory();
                break;
            default:
                String condition = ChatColor.stripColor(itemMeta.getDisplayName());
                customDrop.removeCondition(condition);
                super.open();
                break;
        }
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent e) {
        ConfigurationSection section;
        CustomDrop customDrop = playerGUI.getCustomDrop();
        if(isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.blocks." + playerGUI.getString() + ".drops." + customDrop.getNumber());
        else if(!isBlock && !isFishing) section = instance.getCustomDropsFile().getConfigurationSection("customDrops.entities." + playerGUI.getString() + ".drops." + customDrop.getNumber());
        else section = instance.getCustomDropsFile().getConfigurationSection("customDrops.fishing.drops." + customDrop.getNumber());

        if(section != null) {
            List<String> conditions = customDrop.getConditions();
            section.set("conditions", conditions);
            instance.saveCustomDropsFile();
            instance.reloadCustomDropManager();
        }
    }

}
