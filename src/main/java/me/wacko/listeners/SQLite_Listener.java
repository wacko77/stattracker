package me.wacko.listeners;

import me.wacko.StatTracker;
import me.wacko.db.SQLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class SQLite_Listener implements Listener {
    private final StatTracker plugin;
    private SQLite sqLite;

    public SQLite_Listener(StatTracker plugin, SQLite sqLite) {
        this.plugin = plugin;
        this.sqLite = sqLite;
    }

    @EventHandler
    public void inJoin(PlayerJoinEvent e) {

        if (!e.getPlayer().hasPlayedBefore()){
            this.plugin.getSqLite().addPlayerAsync(e.getPlayer());
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            incrementKills(killer);
        }

        incrementDeaths(victim);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        incrementBlocksBroken(player);
    }

    private void incrementKills(Player player) {
        try {
            sqLite = plugin.getSqLite();
            int kills = sqLite.getPlayerKills(player.getUniqueId().toString());
            sqLite.updatePlayerKillsAsync(player, kills + 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void incrementDeaths(Player player) {
        try {
            sqLite = plugin.getSqLite();
            int deaths = sqLite.getPlayerDeaths(player.getUniqueId().toString());
            sqLite.updatePlayerDeathsAsync(player, deaths + 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void incrementBlocksBroken(Player player) {
        try {
            sqLite = plugin.getSqLite();
            int blocks_broken = sqLite.getPlayerBlocksBroken(player.getUniqueId().toString());
            sqLite.updatePlayerBlocksBrokenAsync(player, blocks_broken + 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
