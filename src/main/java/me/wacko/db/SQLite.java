package me.wacko.db;

import me.wacko.StatTracker;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

public class SQLite {
    public static Connection connection = null;

    private static StatTracker plugin;

    public SQLite(String path, StatTracker plugin) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.plugin = plugin;
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    kills INTEGER DEFAULT 0,
                    deaths INTEGER DEFAULT 0,
                    blocks_broken INTEGER DEFAULT 0)
                    """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static void addPlayerAsync(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    addPlayer(player);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(StatTracker.getInstance(plugin));
    }

    public static void addPlayer(Player player) throws SQLException {
        String uuid = player.getUniqueId().toString();
        int kills = getPlayerKills(uuid);
        int deaths = getPlayerDeaths(uuid);
        int blocks_broken = getPlayerBlocksBroken(uuid);

        if (connection == null) {
            System.out.println("Connection is null.");
            return;
        }

        String sql = "INSERT OR REPLACE INTO players (uuid, kills, deaths, blocks_broken) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setInt(2, kills);
            preparedStatement.setInt(3, deaths);
            preparedStatement.setInt(4, blocks_broken);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            // Handle SQL exception
            ex.printStackTrace();
        }
    }

    public static int getPlayerKills(String uuid) throws SQLException {
        return getPlayerStat(uuid, "kills");
    }

    public static int getPlayerDeaths(String uuid) throws SQLException {
        return getPlayerStat(uuid, "deaths");
    }

    public static int getPlayerBlocksBroken(String uuid) throws SQLException {
        return getPlayerStat(uuid, "blocks_broken");
    }

    public static boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private static int getPlayerStat(String uuid, String columnName) throws SQLException {
        String sql = "SELECT " + columnName + " FROM players WHERE uuid=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            return getPlayerStatFromResultSet(statement.executeQuery(), columnName);
        }
    }

    private static int getPlayerStatFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        if (resultSet.next()) {
            return resultSet.getInt(columnName);
        }
        return 0;
    }

    public static void updatePlayerKillsAsync(Player player, int kills) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updatePlayerKills(player, kills);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(StatTracker.getInstance(plugin));
    }

    public static void updatePlayerKills(Player player, int kills) throws SQLException{

        //if the player doesn't exist, add them
        if (!playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET kills = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, kills);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public static void updatePlayerDeathsAsync(Player player, int deaths) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updatePlayerDeaths(player, deaths);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(StatTracker.getInstance(plugin));
    }

    public static void updatePlayerDeaths(Player player, int deaths) throws SQLException {

        if (!playerExists(player)){
            addPlayer(player);
        }

        String sql = "UPDATE players SET deaths=? WHERE uuid=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, deaths);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        }

    }

    public static void updatePlayerBlocksBrokenAsync(Player player, int blocks_broken) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updatePlayerBlocksBroken(player, blocks_broken);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(StatTracker.getInstance(plugin));
    }

    public static void updatePlayerBlocksBroken(Player player, int blocks_broken) throws SQLException{

        //if the player doesn't exist, add them
        if (!playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET blocks_broken = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, blocks_broken);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }


}


