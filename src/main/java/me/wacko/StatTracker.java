package me.wacko;

import me.wacko.commands.GetStats;
import me.wacko.commands.SetStats;
import me.wacko.db.MySQL;
import me.wacko.db.SQLite;
import me.wacko.listeners.MySQL_Listener;
import me.wacko.listeners.SQLite_Listener;
import me.wacko.placeholders.StatsExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class StatTracker extends JavaPlugin { ;
    private SQLite sqLite;
    private MySQL mySQL;
    private FileConfiguration config;
    private Connection connection;

    @Override
    public void onEnable() {

        System.out.println("[StatTracker] Plugin started...");

        saveDefaultConfig();
        config = getConfig();
        String sqlitePath = config.getString("sqlite.path");

        this.mySQL = new MySQL();

        try {
            this.mySQL.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[Stat Tracker] Could not initialize MySQL.");
        }

        try {

            // Ensure the plugin's data folder exists
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            sqLite = new SQLite(sqlitePath, this);
            System.out.println("[Stat Tracker] Connected to SQLite");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new StatsExpansion(this, sqLite).register();
        }

        Bukkit.getPluginManager().registerEvents(new SQLite_Listener(this, sqLite), this);
        Bukkit.getPluginManager().registerEvents(new MySQL_Listener(mySQL), this);

        getCommand("setstats").setExecutor(new SetStats(this, sqLite));
        getCommand("stats").setExecutor(new GetStats(this, sqLite));

    }

    public SQLite getSqLite(){
        return sqLite;
    }

    public static Plugin getInstance(StatTracker instance) {return instance;}

    @Override
    public void onDisable() {
        try {
            sqLite.closeConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
