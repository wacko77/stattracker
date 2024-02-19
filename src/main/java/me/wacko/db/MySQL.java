package me.wacko.db;

import me.wacko.models.PlayerStats;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQL {
    private Connection connection;

    private FileConfiguration config;

    private Map<String, PlayerStats> playerStatsCache;

    public MySQL(FileConfiguration config) {
        this.config = config;
        this.playerStatsCache = new HashMap<>();
    }

    public Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        String host = config.getString("mysql.host");
        int port = config.getInt("mysql.port");
        String database = config.getString("mysql.database");
        String user = config.getString("mysql.username");
        String password = config.getString("mysql.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("[StatTracker] Connected to MySQL database.");
        return connection;
    }

    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();

        //Create the player_stats table
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (uuid varchar(36) primary key, deaths int, kills int, blocks_broken long, balance double, last_login DATE, last_logout DATE)";

        statement.execute(sql);

        statement.close();

    }

    public PlayerStats findPlayerStatsByUUID(String uuid) throws SQLException {

        if(playerStatsCache.containsKey(uuid)){
            return playerStatsCache.get(uuid);
        } else {

            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM player_stats WHERE uuid = ?");
            statement.setString(1, uuid);

            ResultSet resultSet = statement.executeQuery();

            PlayerStats playerStats = null;

            if(resultSet.next()){
                playerStats = new PlayerStats(
                        resultSet.getString("uuid"),
                        resultSet.getInt("deaths"),
                        resultSet.getInt("kills"),
                        resultSet.getLong("blocks_broken"),
                        resultSet.getDouble("balance"),
                        resultSet.getDate("last_login"),
                        resultSet.getDate("last_logout")
                );
                playerStatsCache.put(uuid, playerStats);
            }
            statement.close();
            return playerStats;
        }
    }

    public void createPlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_stats(uuid, deaths, kills, blocks_broken, balance, last_login, last_logout) VALUES (?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, playerStats.getUuid());
        statement.setInt(2, playerStats.getDeaths());
        statement.setInt(3, playerStats.getKills());
        statement.setLong(4, playerStats.getBlocksBroken());
        statement.setDouble(5, playerStats.getBalance());
        statement.setDate(6, new Date(playerStats.getLastLogin().getTime()));
        statement.setDate(7, new Date(playerStats.getLastLogout().getTime()));

        statement.executeUpdate();

        statement.close();

    }

    public void updatePlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE player_stats SET deaths = ?, kills = ?, blocks_broken = ?, balance = ?, last_login = ?, last_logout = ? WHERE uuid = ?");
        statement.setInt(1, playerStats.getDeaths());
        statement.setInt(2, playerStats.getKills());
        statement.setLong(3, playerStats.getBlocksBroken());
        statement.setDouble(4, playerStats.getBalance());
        statement.setDate(5, new Date(playerStats.getLastLogin().getTime()));
        statement.setDate(6, new Date(playerStats.getLastLogout().getTime()));
        statement.setString(7, playerStats.getUuid());

        statement.executeUpdate();

        statement.close();

    }

    public void deletePlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM player_stats WHERE uuid = ?");
        statement.setString(1, playerStats.getUuid());

        statement.executeUpdate();

        statement.close();

    }
}
