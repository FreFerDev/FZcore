package com.ferozity.fzcore.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownUtil {

    private static final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    public static void setCooldown(UUID playerId, String action, int seconds) {
        cooldowns.computeIfAbsent(action, k -> new HashMap<>()).put(playerId, System.currentTimeMillis() + (seconds * 1000L));
    }

    public static boolean isOnCooldown(UUID playerId, String action) {
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) return false;
        
        Long expiresAt = actionCooldowns.get(playerId);
        if (expiresAt == null) return false;
        
        if (System.currentTimeMillis() >= expiresAt) {
            actionCooldowns.remove(playerId);
            if (actionCooldowns.isEmpty()) {
                cooldowns.remove(action);
            }
            return false;
        }
        return true;
    }

    public static long getRemainingSeconds(UUID playerId, String action) {
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) return 0;
        
        Long expiresAt = actionCooldowns.get(playerId);
        if (expiresAt == null) return 0;
        
        long remaining = (expiresAt - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    public static void removeCooldown(UUID playerId, String action) {
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns != null) {
            actionCooldowns.remove(playerId);
            if (actionCooldowns.isEmpty()) {
                cooldowns.remove(action);
            }
        }
    }

    public static void clearAll() {
        cooldowns.clear();
    }
}