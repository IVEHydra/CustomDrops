package me.ivehydra.customdrops.listeners;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.condition.ConditionType;
import me.ivehydra.customdrops.condition.Variable;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.gui.PlayerGUI;
import me.ivehydra.customdrops.utils.MaterialUtils;
import me.ivehydra.customdrops.utils.MessageUtils;
import me.ivehydra.customdrops.utils.StringUtils;
import me.ivehydra.customdrops.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AsyncPlayerChatListener implements Listener {

    private final CustomDrops instance = CustomDrops.getInstance();

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();

        if(instance.containsWaitingPlayer(p)) {
            e.setCancelled(true);
            handleBlockOrEntity(p, message);
        } else if(instance.containsWaitingCPlayer(p)) {
            e.setCancelled(true);
            handleConditionOrAction(p, message);
        }
    }

    private void handleBlockOrEntity(Player p, String message) {
        message = message.trim();

        if(message.equalsIgnoreCase("cancel")) {
            instance.removeWaitingPlayer(p);
            execute(p);
            return;
        }

        boolean isBlock = instance.getWaitingPlayer(p);

        if(isBlock) handleBlock(p, message);
        else handleEntity(p, message);

    }

    private void handleBlock(Player p, String block) {
        Material material = MaterialUtils.parse(block);
        if(material != null && material.isBlock()) {
            String path = "customDrops.blocks." + material.name();
            if(!instance.getCustomDropsFile().contains(path)) {
                instance.removeWaitingPlayer(p);
                addBlock(path);
                reload(p);
            } else p.sendMessage(MessageUtils.BLOCK_ALREADY_EXIST.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%block_name%", block));
        } else p.sendMessage(MessageUtils.BLOCK_INVALID.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%block_name%", block));
    }

    private void handleEntity(Player p, String string) {
        try {
            EntityType entity = EntityType.valueOf(string.toUpperCase());
            String path = "customDrops.entities." + entity.name();
            if(!instance.getCustomDropsFile().contains(path)) {
                instance.removeWaitingPlayer(p);
                addEntity(path);
                reload(p);
            } else p.sendMessage(MessageUtils.ENTITY_ALREADY_EXIST.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%entity_name%", string));
        } catch(IllegalArgumentException e) {
            p.sendMessage(MessageUtils.ENTITY_INVALID.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%entity_name%", string));
        }
    }

    private void handleConditionOrAction(Player p, String message) {
        if(message.equalsIgnoreCase("cancel") || message.startsWith("cancel")) {
            instance.removeWaitingCPlayer(p);
            execute(p);
            return;
        }

        boolean isCondition = instance.getWaitingCPlayer(p);

        if(isCondition) handleCondition(p, message);
        else handleAction(p, message);

    }

    private void handleCondition(Player p, String message) {
        String[] args = message.split(" ");
        if(args.length < 3) {
            instance.getConfig().getStringList("messages.condition.help").forEach(string -> p.sendMessage(StringUtils.getColoredString(string).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return;
        }

        String placeholder = args[0];
        String type = args[1];
        String value = message.substring(placeholder.length() + type.length() + 2);

        if(!Variable.isValidPlaceholder(placeholder, p)) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.condition.invalid.placeholder")).replace("%placeholder%", placeholder).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return;
        }

        ConditionType conditionType;
        try {
            conditionType = ConditionType.fromString(type);
        } catch(IllegalArgumentException exception) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.condition.invalid.conditionType")).replace("%conditionType%", type).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return;
        }

        instance.removeWaitingCPlayer(p);
        String condition = placeholder + " " + conditionType.getOperator() + " " + value;
        addCondition(p, condition);
        reload(p);
    }

    private void handleAction(Player p, String message) {
       String action = org.apache.commons.lang.StringUtils.substringBetween(message, "[", "]");
       if(action == null) {
           p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.action.invalid.action")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
           return;
       }

       String args = message.split(" ", 2)[1];

        if(!validateAction(p, action, args)) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.action.invalid.action")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return;
        }

        instance.removeWaitingCPlayer(p);
        addAction(p, "[" + action + "]" + " " + args);
        reload(p);
    }

    private boolean validateAction(Player p, String action, String args) {
        switch(action.toUpperCase()) {
            case "SOUND":
                return validateSound(p, args);
            case "TITLE":
                return validateTitle(p, args);
            case "MESSAGE":
            case "JSON_MESSAGE":
            case "ACTIONBAR":
            case "COMMAND_CONSOLE":
            case "COMMAND_PLAYER":
                return true;
            default:
                return false;
        }
    }

    private void execute(Player p) {
        Titles.clearTitle(p);
        if(VersionUtils.isAtLeastVersion113()) Bukkit.getScheduler().runTask(instance, () -> p.performCommand("customdrops"));
        else p.performCommand("customdrops");
    }

    private void reload(Player p) {
        instance.reloadCustomDropManager();
        execute(p);
    }

    private void addBlock(String path) {
        addDefault(path);
        instance.getCustomDropsFile().set(path + ".drops.0.disableForPlaced", false);
        instance.getCustomDropsFile().set(path + ".drops.0.drop.type", "ITEM");
        instance.getCustomDropsFile().set(path + ".drops.0.drop.item.==", "org.bukkit.inventory.ItemStack");
        instance.getCustomDropsFile().set(path + ".drops.0.drop.item.type", "COBBLESTONE");
        instance.getCustomDropsFile().set(path + ".drops.0.drop.actions", Collections.singletonList("[MESSAGE] &eTest"));
        instance.getCustomDropsFile().set(path + ".drops.0.exp", 1);
        instance.saveCustomDropsFile();
    }

    private void addEntity(String path) {
        addDefault(path);
        instance.getCustomDropsFile().set(path + ".drops.0.disableForSpawner", false);
        instance.getCustomDropsFile().set(path + ".drops.0.disableForSpawnerEgg", false);
        instance.getCustomDropsFile().set(path + ".drops.0.drop.type", "ITEM");
        instance.getCustomDropsFile().set(path + ".drops.0.drop.item.==", "org.bukkit.inventory.ItemStack");
        instance.getCustomDropsFile().set(path + ".drops.0.drop.item.type", "COOKED_BEEF");
        instance.getCustomDropsFile().set(path + ".drops.0.drop.actions", Collections.singletonList("[MESSAGE] &eTest"));
        instance.getCustomDropsFile().set(path + ".drops.0.exp", 1);
        instance.saveCustomDropsFile();
    }

    private void addDefault(String path) {
        instance.getCustomDropsFile().set(path + ".enabled", true);
        instance.getCustomDropsFile().set(path + ".vanillaDrops.disabled", false);
        instance.getCustomDropsFile().set(path + ".vanillaDrops.worlds", Collections.singletonList("world"));
        instance.getCustomDropsFile().set(path + ".vanillaEXP.disabled", false);
        instance.getCustomDropsFile().set(path + ".vanillaEXP.worlds", Collections.singletonList("world"));
        instance.getCustomDropsFile().set(path + ".drops.0.conditions", Collections.singletonList("%player_world% equalsIgnoreCase world"));
        instance.getCustomDropsFile().set(path + ".drops.0.chance", 0.02);
        instance.getCustomDropsFile().set(path + ".drops.0.disableForNatural", false);
    }

    private void addCondition(Player p, String condition) {
        PlayerGUI playerGUI = instance.getPlayerGUI(p);
        CustomDrop customDrop = playerGUI.getCustomDrop();
        String path = getPath(playerGUI);

        customDrop.addCondition(condition);
        List<String> conditions = customDrop.getConditions();
        instance.getCustomDropsFile().set(path + ".conditions", conditions);
        instance.saveCustomDropsFile();
        instance.reloadCustomDropManager();
    }

    private void addAction(Player p, String action) {
        PlayerGUI playerGUI = instance.getPlayerGUI(p);
        CustomDrop customDrop = playerGUI.getCustomDrop();
        String path = getPath(playerGUI);

        customDrop.addAction(action);
        List<String> actions = customDrop.getActions();
        instance.getCustomDropsFile().set(path + ".drop.actions", actions);
        instance.saveCustomDropsFile();
        instance.reloadCustomDropManager();
    }

    private String getPath(PlayerGUI playerGUI) {
        CustomDrop customDrop = playerGUI.getCustomDrop();
        boolean isBlock = playerGUI.isBlock();
        boolean isFishing = playerGUI.isFishing();

        if(isBlock && !isFishing) return "customDrops.blocks." + playerGUI.getString() + ".drops." + customDrop.getNumber();
        else if(!isBlock && !isFishing) return "customDrops.entities." + playerGUI.getString() + ".drops." + customDrop.getNumber();
        else return "customDrops.fishing.drops." + customDrop.getNumber();
    }

    private boolean validateSound(Player p, String string) {
        String[] args = string.split(";");

        if(args.length != 3) return false;

        if(args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty()) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.action.invalid.action")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return false;
        }

        Optional<XSound> sound = XSound.matchXSound(args[0]);
        if(!sound.isPresent()) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.action.invalid.sound")).replace("%sound_name%", args[0]).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return false;
        }

        try {
            Double.parseDouble(args[1]);
            Double.parseDouble(args[2]);
        } catch(NumberFormatException e) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("message.action.invalid.number")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return false;
        }

        return true;
    }

    private boolean validateTitle(Player p, String string) {
        String[] args = string.split(";");

        if(args.length != 5) return false;

        if(args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty() || args[3].isEmpty() || args[4].isEmpty()) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("messages.action.invalid.action")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return false;
        }

        try {
            Integer.parseInt(args[2]);
            Integer.parseInt(args[3]);
            Integer.parseInt(args[4]);
        } catch(NumberFormatException e) {
            p.sendMessage(StringUtils.getColoredString(Objects.requireNonNull(instance.getConfig().getString("message.action.invalid.number")).replace("%prefix%", Objects.requireNonNull(instance.getConfig().getString("messages.prefix")))));
            return false;
        }

        return true;
    }

}
