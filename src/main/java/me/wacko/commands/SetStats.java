package me.wacko.commands;

import me.wacko.StatTracker;
import me.wacko.db.SQLite;
import me.wacko.util.ItemStack_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class SetStats implements CommandExecutor {

    private final StatTracker plugin;
    private SQLite sqLite;

    public SetStats(StatTracker plugin, SQLite sqLite) {
        this.plugin = plugin;
        this.sqLite = sqLite;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length != 3){
            sender.sendMessage(ChatColor.YELLOW + "Run it like /setstats <player> <stat> <value>");
            return true;
        }

        //Get the player
        Player player = sender.getServer().getPlayer(args[0]);
        if (player == null){
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        String stat = args[1].toLowerCase();
        int value;
        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid value. Please provide a number.");
            return true;
        }

        try {
            switch (stat){
                case "kills":
                    sqLite.updatePlayerKills(player, value);
                    sender.sendMessage("Kills set to " + value + " for player " + player.getName());
                    break;
                case "deaths":
                    sqLite.updatePlayerDeaths(player, value);
                    sender.sendMessage("Deaths set to " + value + " for player " + player.getName());
                    break;
                case "blocksbroken":
                    sqLite.updatePlayerBlocksBroken(player, value);
                    sender.sendMessage("Blocks broken set to " + value + " for player " + player.getName());
                    break;
                default:
                    sender.sendMessage("Invalid stat. Supported stats: kills, deaths, blocksbroken");
                    return true;
            }
        } catch (SQLException e) {
            sender.sendMessage("An error occurred while updating player stats. Please check the server logs.");
        }

        return true;
    }

}
