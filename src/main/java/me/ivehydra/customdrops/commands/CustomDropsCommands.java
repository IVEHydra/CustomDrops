package me.ivehydra.customdrops.commands;

import me.ivehydra.customdrops.CustomDrops;
import me.ivehydra.customdrops.customdrop.CustomDrop;
import me.ivehydra.customdrops.customdrop.CustomDropHandler;
import me.ivehydra.customdrops.customdrop.CustomDropManager;
import me.ivehydra.customdrops.gui.guis.CustomDropEditingGUI;
import me.ivehydra.customdrops.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomDropsCommands implements CommandExecutor {

    private final CustomDrops instance = CustomDrops.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(cmd.getName().equalsIgnoreCase("customdrops")) {
            if(!(sender instanceof Player)) {
                switch(args.length) {
                    case 0:
                        sender.sendMessage(MessageUtils.NO_PLAYER.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        break;
                    case 1:
                        if(args[0].equalsIgnoreCase("help")) {
                            sendHelp(sender);
                        } else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                            instance.reload();
                            sender.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        } else {
                            sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        break;
                    case 3:
                        if(args[0].equalsIgnoreCase("edit")) {
                            sender.sendMessage(MessageUtils.NO_PLAYER.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        break;
                    default:
                        sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        break;
                }
            } else {
                Player p = (Player) sender;
                switch(args.length) {
                    case 0:
                        if(!p.hasPermission("customdrops.help")) {
                            sendNoHelp(p);
                            return true;
                        }
                        sendHelp(p);
                        break;
                    case 1:
                        if(args[0].equalsIgnoreCase("help")) {
                            if(!p.hasPermission("customdrops.help")) {
                                sendNoHelp(p);
                                return true;
                            }
                            sendHelp(p);
                        } else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                            if(!p.hasPermission("customdrops.reload")) {
                                p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                                return true;
                            }
                            instance.reload();
                            p.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        } else {
                            p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        break;
                    case 3:
                        if(args[0].equalsIgnoreCase("edit")) {
                            if(!p.hasPermission("customdrops.editor")) {
                                p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                                return true;
                            }
                            String name = args[1];
                            String number = args[2];
                            CustomDropManager customDropManager = instance.getCustomDropManager();
                            CustomDrop customDrop = getCustomDrop(name, number, customDropManager);
                            if(customDrop != null) {
                                if(name.equalsIgnoreCase("fishing"))
                                    new CustomDropEditingGUI(instance.getPlayerGUI(p), "", "fishing", customDrop).open();
                                else if(customDropManager.getBlockNames().contains(name))
                                    new CustomDropEditingGUI(instance.getPlayerGUI(p), name, "blocks", customDrop).open();
                                else if(customDropManager.getEntityNames().contains(name))
                                    new CustomDropEditingGUI(instance.getPlayerGUI(p), name, "entities", customDrop).open();
                            } else {
                                p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                                return true;
                            }
                        } else {
                            p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        break;
                    default:
                        p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        break;
                }
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.YELLOW + "CustomDrops by IVEHydra" + ChatColor.GRAY + " v" + ChatColor.YELLOW + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
        sender.sendMessage(ChatColor.YELLOW + "Commands:");
        sender.sendMessage(ChatColor.YELLOW + "/customdrops edit <block name/entity name/fishing> <drop number>" + ChatColor.GRAY + " - Opens the Custom Drop Editor GUI.");
        sender.sendMessage(ChatColor.YELLOW + "/customdrops help" + ChatColor.GRAY + " - Sends a message with all commands and permissions.");
        sender.sendMessage(ChatColor.YELLOW + "/customdrops reload | rl" + ChatColor.GRAY + " - Reloads the configuration file and the Custom Drops file.");
        sender.sendMessage(ChatColor.YELLOW + "Permissions:");
        sender.sendMessage(ChatColor.YELLOW + "customdrops.*" + ChatColor.GRAY + " - Allows to execute all commands.");
        sender.sendMessage(ChatColor.YELLOW + "customdrops.editor" + ChatColor.GRAY + " - Allows to open the Custom Drop Editor GUI.");
        sender.sendMessage(ChatColor.YELLOW + "customdrops.help" + ChatColor.GRAY + " - Allows to see all commands and permissions.");
        sender.sendMessage(ChatColor.YELLOW + "customdrops.reload" + ChatColor.GRAY + " - Allows to reload the configuration file and the Custom Drops file.");
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.YELLOW + "CustomDrops by IVEHydra" + ChatColor.GRAY + " v" + ChatColor.YELLOW + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
    }

    private void sendNoHelp(CommandSender sender) { sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.YELLOW + "CustomDrops by IVEHydra" + ChatColor.GRAY + " v" + ChatColor.YELLOW + instance.getDescription().getVersion() + ChatColor.GRAY + " -------"); }

    private CustomDrop getCustomDrop(String name, String number, CustomDropManager customDropManager) {
        CustomDropHandler customDropHandler = getCustomDropHandler(name, customDropManager);

        if(customDropHandler == null)
            return null;

        return customDropHandler.getCustomDrops().stream()
                .filter(customDrop -> customDrop.getNumber().equals(number))
                .findFirst()
                .orElse(null);

    }

    private CustomDropHandler getCustomDropHandler(String name, CustomDropManager customDropManager) {
        if(name.equalsIgnoreCase("fishing"))
            return customDropManager.getFishingCustomDrops();

        if(customDropManager.getBlockNames().contains(name))
            return customDropManager.getBlockCustomDrops().get(name);

        if(customDropManager.getEntityNames().contains(name))
            return customDropManager.getEntityCustomDrops().get(name);

        return null;
    }

}
