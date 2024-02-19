package me.wacko.models;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatsCache {

    private static Map<String, PlayerStats> cache = new HashMap<>();

    public static void addToCache(String uuid, PlayerStats playerStats) {
        cache.put(uuid, playerStats);
    }

    public static PlayerStats getFromCache(String uuid) {
        return cache.get(uuid);
    }

    public static boolean isInCache(String uuid) {
        return cache.containsKey(uuid);
    }

    public static void removeFromCache(String uuid) {
        cache.remove(uuid);
    }

}
