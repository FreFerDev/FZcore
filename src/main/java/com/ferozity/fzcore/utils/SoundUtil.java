package com.ferozity.fzcore.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playErrorSound(Player player) {
        if (player == null || !player.isOnline()) return;
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        if (player == null || !player.isOnline()) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}