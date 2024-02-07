package me.wacko;

import me.wacko.db.Database;
import me.wacko.listeners.Listeners;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class StatTracker extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {

        System.out.println("[StatTracker] Plugin started...");

        //JDBC - Java Database Connectivity API
        this.database = new Database();
        try {
            this.database.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[StatTracker] Could not initialize database.");
        }

        getServer().getPluginManager().registerEvents(new Listeners(database), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
