package me.wacko.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ItemStack_Util {

    public static ItemStack getItem(String name, Material mat, int amount, Player player, String... lore){

        if(mat != Material.PLAYER_HEAD) {
            ItemStack i = new ItemStack(mat, amount);

            ItemMeta meta = i.getItemMeta();

            meta.setDisplayName(name);

            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            meta.setLore(Arrays.asList(lore));

            i.setItemMeta(meta);

            return i;
        } else if (mat == Material.PLAYER_HEAD){

            ItemStack sItem = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta sMeta = (SkullMeta) sItem.getItemMeta();

            sMeta.setDisplayName(name);

            sMeta.setLore(Arrays.asList(lore));

            sMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));

            sItem.setItemMeta(sMeta);

            return sItem;
        }
        return getItem(name, mat, amount, player, lore);
    }

}
