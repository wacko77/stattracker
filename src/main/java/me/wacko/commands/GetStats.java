package me.wacko.commands;

import me.clip.placeholderapi.PlaceholderAPI;
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
import java.util.Arrays;
import java.util.Collections;
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

        List<String> lore = getStrings(player);

        Inventory stats = Bukkit.createInventory(player, 9*3, user + "'s Stats");
        ItemStack display = ItemStack_Util.getItem("Stats", Material.PLAYER_HEAD, 1, player, lore);


        stats.setItem(13, display);

        player.openInventory(stats);

        return true;
    }

    @NotNull
    private static List<String> getStrings(Player p) {
        List<String> lore = new ArrayList<String>();

        String killsPlaceholder = "%stat-tracker_kills%";
        String deathsPlaceholder = "%stat-tracker_deaths%";
        String blocksBrokenPlaceholder = "%stat-tracker_blocksbroken%";

        String killsValue = PlaceholderAPI.setPlaceholders(p, killsPlaceholder);
        String deathsValue = PlaceholderAPI.setPlaceholders(p, deathsPlaceholder);
        String blocksBrokenValue = PlaceholderAPI.setPlaceholders(p, blocksBrokenPlaceholder);

        lore.addAll(Arrays.asList(
                "Kills: " + killsValue,
                "Deaths: " + deathsValue,
                "Blocks Broken: " + blocksBrokenValue));

        return lore;
    }

}
