package me.wacko.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wacko.StatTracker;
import me.wacko.db.SQLite;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class StatsExpansion extends PlaceholderExpansion {

    private final StatTracker plugin;
    private SQLite sqLite;

    public StatsExpansion(StatTracker plugin, SQLite sqLite){
        this.plugin = plugin;
        this.sqLite = sqLite;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "stat-tracker";
    }

    @Override
    public @NotNull String getAuthor() {
        return "wacko";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) {
            return null;
        }

        int kills;
        int deaths;
        int blocksBroken;

        if (identifier.equalsIgnoreCase("kills")) {
            try {
                kills = sqLite.getPlayerKills(player.getUniqueId().toString());
                return String.valueOf(kills);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (identifier.equalsIgnoreCase("deaths")) {
            try {
                deaths = sqLite.getPlayerDeaths(player.getUniqueId().toString());
                return String.valueOf(deaths);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (identifier.equalsIgnoreCase("blocksbroken")) {
            try {
                blocksBroken = sqLite.getPlayerBlocksBroken(player.getUniqueId().toString());
                return String.valueOf(blocksBroken);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
