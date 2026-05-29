package com.ferozity.fzcore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    public static String toMiniMessage(String legacyText) {
        Component component = LEGACY_SERIALIZER.deserialize(legacyText);
        return MINI_MESSAGE.serialize(component);
    }

    public static String toLegacy(String miniMessageText) {
        Component component = MINI_MESSAGE.deserialize(miniMessageText);
        return LEGACY_SERIALIZER.serialize(component);
    }

    public static String colorize(String message) {
        return ColorUtil.colorize(message);
    }

    public static void sendMessage(Player player, String message) {
        if (player == null || !player.isOnline()) return;
        player.sendMessage(ColorUtil.colorize(message));
    }

    public static String gradient(String text, String startColor, String endColor) {
        return "<gradient:" + startColor + ":" + endColor + ">" + text + "</gradient>";
    }

    public static String rainbow(String text) {
        return "<rainbow>" + text + "</rainbow>";
    }

    public static String clickCommand(String text, String command) {
        return "<click:run_command:" + command + ">" + text + "</click>";
    }

    public static String clickUrl(String text, String url) {
        return "<click:open_url:" + url + ">" + text + "</click>";
    }

    public static String hoverText(String text, String hover) {
        return "<hover:show_text:'" + hover + "'>" + text + "</hover>";
    }

    public static String gradientHover(String text, String startColor, String endColor, String hover) {
        return "<gradient:" + startColor + ":" + endColor + "><hover:show_text:'" + hover + "'>" + text + "</hover></gradient>";
    }
}