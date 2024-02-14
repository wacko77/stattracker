package me.wacko;

import me.wacko.commands.GetStats;
import me.wacko.commands.SetStats;
import me.wacko.db.MySQL;
import me.wacko.db.SQLite;
import me.wacko.listeners.SQLite_Listener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class StatTracker extends JavaPlugin { ;
    private SQLite sqLite;
    private MySQL mySQL;

    @Override
    public void onEnable() {

        System.out.println("[StatTracker] Plugin started...");

        try {
            // Ensure the plugin's data folder exists
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            sqLite = new SQLite(getDataFolder().getAbsolutePath() + "/stat_tracker.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getPluginManager().registerEvents(new SQLite_Listener(this), this);

        getCommand("setstats").setExecutor(new SetStats(this, sqLite));
        getCommand("stats").setExecutor(new GetStats(this, sqLite));

    }

    public SQLite getSqLite(){
        return sqLite;
    }

    @Override
    public void onDisable() {
        try {
            sqLite.closeConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
