package net.casheh.celllevel.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.UUID;

public class Util {

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static int getMaterialCount(Material material, Inventory inv) {
        int count= 0;

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
                continue;
            if (inv.getItem(i).getType() == material)
                count += inv.getItem(i).getAmount();
        }
        return count;
    }

    public static ItemStack removeAllText(ItemStack rawItem) {
        ItemStack item = new ItemStack(rawItem);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        meta.setLore(Collections.emptyList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getSkull(Skull skullType) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", skullType.getTexture()));

        try {
            Field profileField  = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception ex) {
            ex.printStackTrace();;
        }
        skull.setItemMeta(meta);
        return skull;
    }

    public static int getEmptySpace(PlayerInventory inventory, Material material) {
        int count = 0;

        for (int i = 0; i < inventory.getSize() - 5; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                count += material.getMaxStackSize();
                continue;
            }

            if (item.getType() == material)
                count += (material.getMaxStackSize() - item.getAmount());

        }
        return count;
    }

//    public static int getUsesLeft(ItemStack wand) {
//
//    }
}
