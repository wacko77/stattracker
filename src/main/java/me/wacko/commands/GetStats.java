package me.wacko.commands;

import me.wacko.StatTracker;
import me.wacko.db.SQLite;
import me.wacko.util.ItemStack_Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetStats implements CommandExecutor {

    private final StatTracker plugin;
    private SQLite sqLite;

    public GetStats(StatTracker plugin, SQLite sqLite) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by players.");
            return true;
        }

        Player player = (Player) sender;
        String user = player.getDisplayName();

        // Fetch and display player's stats
        try {

            int kills = sqLite.getPlayerKills(player.getUniqueId().toString());
            int deaths = sqLite.getPlayerDeaths(player.getUniqueId().toString());
            int blocksBroken = sqLite.getPlayerBlocksBroken(player.getUniqueId().toString());

            List<String> lore = getStrings(kills, deaths, blocksBroken);

            Inventory stats = Bukkit.createInventory(player, 9*3, user + "'s Stats");
            ItemStack display = ItemStack_Util.getItem("Stats", Material.PLAYER_HEAD, 1, player, String.valueOf(lore));


            stats.setItem(13, display);

            player.openInventory(stats);

        } catch (SQLException ex) {
            player.sendMessage("An error occurred while fetching your stats. Please check the server logs.");
        }

        return true;
    }

    @NotNull
    private static List<String> getStrings(int kills, int deaths, int blocksBroken) {
        List<String> lore = new ArrayList<>();

        String killsPlaceholder = "%kills%";
        String deathsPlaceholder = "%deaths%";
        String blocksBrokenPlaceholder = "%blocksbroken%";

        lore.add("Kills: " + killsPlaceholder);
        lore.add("Deaths: " + deathsPlaceholder);
        lore.add("Blocks Broken: " + blocksBrokenPlaceholder);

        // Set placeholders with actual values
        lore.set(0, lore.get(0).replace(killsPlaceholder, String.valueOf(kills)));
        lore.set(1, lore.get(1).replace(deathsPlaceholder, String.valueOf(deaths)));
        lore.set(2, lore.get(2).replace(blocksBrokenPlaceholder, String.valueOf(blocksBroken)));
        return lore;
    }

}
