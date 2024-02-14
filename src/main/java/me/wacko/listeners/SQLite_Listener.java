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

    public SQLite_Listener(StatTracker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inJoin(PlayerJoinEvent e) throws SQLException {

        //if the player is new, add them to the database
        if (!e.getPlayer().hasPlayedBefore()){
            //add the player to the database
            this.plugin.getSqLite().addPlayer(e.getPlayer());
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            // Increment killer's kill count
            incrementKills(killer);
        }

        // Increment victim's death count
        incrementDeaths(victim);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Increment player's blocks broken count
        incrementBlocksBroken(player);
    }

    private void incrementKills(Player player) {
        try {
            sqLite = plugin.getSqLite();
            int kills = sqLite.getPlayerKills(player.getUniqueId().toString());
            sqLite.updatePlayerKills(player, kills + 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void incrementDeaths(Player player) {
        try {
            sqLite = plugin.getSqLite();
            int deaths = sqLite.getPlayerDeaths(player.getUniqueId().toString());
            sqLite.updatePlayerDeaths(player, deaths + 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void incrementBlocksBroken(Player player) {
        try {
            sqLite = plugin.getSqLite();
            int blocks_broken = sqLite.getPlayerBlocksBroken(player.getUniqueId().toString());
            sqLite.updatePlayerBlocksBroken(player, blocks_broken + 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
