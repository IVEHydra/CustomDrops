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
    BLOCK_NAME("messages.block.insertName"),
    BLOCK_ALREADY_EXIST("messages.block.alreadyExist"),
    BLOCK_INVALID("messages.block.invalidName"),
    ENTITY_NAME("messages.entity.insertName"),
    ENTITY_ALREADY_EXIST("messages.entity.alreadyExist"),
    ENTITY_INVALID("messages.entity.invalidName"),
    CONDITION_INSERT("messages.condition.insert"),
    CONDITION_HELP("messages.condition.help"),
    CONDITION_INVALID_PLACEHOLDER("messages.condition.invalid.placeholder"),
    CONDITION_INVALID_CONDITION("messages.condition.invalid.conditionType"),
    ACTION_INSERT("messages.action.insert"),
    ACTION_HELP("messages.action.help"),
    ACTION_INVALID_ACTION("messages.action.invalid.action"),
    ACTION_INVALID_NUMBER("messages.action.invalid.number"),
    ACTION_INVALID_SOUND("messages.action.invalid.sound");

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
