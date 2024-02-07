package me.wacko.sb;

import me.wacko.db.Database;
import me.wacko.models.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public class StatsScoreboard implements Listener {

    private Map<Player, BukkitTask> taskMap = new HashMap<>();
    Plugin plugin;
    Database db;

    public StatsScoreboard(Plugin plugin){
        this.plugin = plugin;
        this.db = new Database("localhost", "player_stats", "root", "");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("[StatTracker] Scoreboard enabled!");
        Player player = event.getPlayer();
        createScoreboard(player);
    }

    private void createScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("CustomScoreboard", "dummy", ChatColor.BOLD + "Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Add lines for kills, deaths, and server balance
        Score killsScore = objective.getScore(ChatColor.YELLOW + "Kills:");

        Score deathsScore = objective.getScore(ChatColor.YELLOW + "Deaths:");

        player.setScoreboard(scoreboard);


        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                cancelTask(player);
                return;
            }

            killsScore.setScore(db.getKills());
            deathsScore.setScore(PlayerStats.getDeaths());

            player.setScoreboard(scoreboard);
        }, 0, 20); // 20 ticks = 1 second

        taskMap.put(player, task);
    }
    private void cancelTask(Player player) {
        BukkitTask task = taskMap.get(player);
        if (task != null) {
            task.cancel();
            taskMap.remove(player);
        }
    }
    }


