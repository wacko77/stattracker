package me.wacko.listeners;

import me.wacko.db.MySQL;
import me.wacko.models.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MySQL_Listener implements Listener {

    private final MySQL database;
    private Map<String, PlayerStats> playerStatsCache;

    public MySQL_Listener(MySQL database) {
        this.database = database;
        this.playerStatsCache = new HashMap<>();
    }

    public PlayerStats getPlayerStatsFromDatabase(Player player) throws SQLException {

        PlayerStats playerStats = database.findPlayerStatsByUUID(player.getUniqueId().toString());

        if (playerStats == null) {
            playerStats = new PlayerStats(player.getUniqueId().toString(), 0, 0, 0, 0.0, new Date(), new Date());
            database.createPlayerStats(playerStats);
        }

        return playerStats;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();
        try{
            PlayerStats playerStats = database.findPlayerStatsByUUID(p.getUniqueId().toString());
            if(playerStats != null){
                playerStats = getPlayerStatsFromDatabase(p);
                playerStatsCache.put(p.getUniqueId().toString(), playerStats);
                playerStats.setLastLogin(new Date());
            }

            /*
            database.updatePlayerStats(playerStats);*/
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("[StatTracker] Could not update player stats after join.");
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){

        Player p = e.getPlayer();
        PlayerStats cachedStats = playerStatsCache.get(p.getUniqueId().toString());

        try{
            database.updatePlayerStats(cachedStats);
            cachedStats.setLastLogout(new Date());

            /*PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            database.updatePlayerStats(playerStats);*/
        }catch (SQLException e1){
            e1.printStackTrace();
            System.out.println("[StatTracker] Could not update player stats after quit.");
        }
        playerStatsCache.remove(p.getUniqueId().toString());

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();
        PlayerStats cachedStats = playerStatsCache.get(p.getUniqueId().toString());

        if (cachedStats != null) {
            cachedStats.setBlocksBroken(cachedStats.getBlocksBroken() + 1);
            // Update the cache
            playerStatsCache.put(p.getUniqueId().toString(), cachedStats);
            try {
                database.updatePlayerStats(cachedStats);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            // Fetch from MySQL if not cached
            try {
                PlayerStats playerStats = database.findPlayerStatsByUUID(p.getUniqueId().toString());
                if (playerStats != null) {
                    playerStats.setBlocksBroken(playerStats.getBlocksBroken() + 1);
                    // Update cache
                    playerStatsCache.put(p.getUniqueId().toString(), playerStats);
                    database.updatePlayerStats(playerStats);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        String uuid = player.getUniqueId().toString();
        PlayerStats cachedStats = playerStatsCache.get(uuid);
        if (cachedStats != null) {
            cachedStats.setDeaths(cachedStats.getDeaths() + 1);
            // Update the cache
            playerStatsCache.put(uuid, cachedStats);
            try {
                database.updatePlayerStats(cachedStats);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            // Fetch from MySQL if not cached
            try {
                PlayerStats playerStats = database.findPlayerStatsByUUID(uuid);
                if (playerStats != null) {
                    playerStats.setDeaths(playerStats.getDeaths() + 1);
                    // Update cache
                    playerStatsCache.put(uuid, playerStats);
                    database.updatePlayerStats(playerStats);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
      }

    }

