package me.ivehydra.customdrops.utils;

import me.ivehydra.customdrops.CustomDrops;

public enum MessageUtils {

    PREFIX("messages.prefix"),
    NO_PLAYER("messages.general.noPlayer"),
    NO_PERMISSION("messages.general.noPermission"),
    CONFIG_RELOADED("messages.general.configReloaded"),
    WRONG_ARGUMENTS("messages.general.wrongArguments"),
    LATEST_VERSION("messages.updateCheck.latestVersion"),
    NEW_VERSION("messages.updateCheck.newVersionAvailable"),
    FIRST_PAGE("messages.gui.firstPage"),
    LAST_PAGE("messages.gui.lastPage"),
    GUI_ALREADY_EXIST("messages.gui.alreadyExist"),
    INVENTORY_FULL("messages.inventory.full");

    private final CustomDrops instance = CustomDrops.getInstance();
    private final String path;

    MessageUtils(String path) { this.path = path; }

    public String getPath() { return path; }

    public String getFormattedMessage(Object... replacements) {
        String message = instance.getConfig().getString(path);
        message = StringUtils.getColoredString(message);
        for(int i = 0; i < replacements.length; i += 2) {
            String placeholder = (String) replacements[i];
            String value = (String) replacements[i + 1];
            message = message.replace(placeholder, value);
        }
        return message;
    }

    @Override
    public String toString() { return getFormattedMessage(); }

}
