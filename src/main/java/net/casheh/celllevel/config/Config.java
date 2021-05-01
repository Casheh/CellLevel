package net.casheh.celllevel.config;

import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.nbt.NBTEditor;
import net.casheh.celllevel.util.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {

    private CellLevel plugin;

    private String prefix;

    private String wandGiven;

    private String noPermission;

    private String noShops;

    private String notAtIsland;

    private String wandName;

    private List<String> wandLore;

    private String cellTopName;

    private List<String> depositMessages;

    private int updateInterval;

    public Config(CellLevel plugin) {
        this.plugin = plugin;
        assign();
    }

    public void assign() {
        plugin.reloadConfig();
        this.wandName = plugin.getConfig().getString("item.wand-name");
        this.prefix = plugin.getConfig().getString("messages.prefix");
        this.wandGiven = plugin.getConfig().getString("messages.wand-given");
        this.noPermission = plugin.getConfig().getString("messages.no-permission");
        this.noShops = plugin.getConfig().getString("messages.no-shops");
        this.notAtIsland = plugin.getConfig().getString("messages.not-at-island");
        this.wandLore = plugin.getConfig().getStringList("item.lore");
        this.cellTopName = plugin.getConfig().getString("menu.celltop.name");
        this.depositMessages = plugin.getConfig().getStringList("deposit.deposit-messages");
        this.updateInterval = plugin.getConfig().getInt("menu.celltop.update-interval");
    }

    public String getPrefix() {
        return Util.color(this.prefix);
    }

    public String getNoPermission() {
        return getPrefix() + Util.color(this.noPermission);
    }

    public String getNoShops() {
        return getPrefix() + Util.color(this.noShops);
    }

    public String getNotAtIsland() {
        return getPrefix() + Util.color(this.notAtIsland);
    }

    public String getWandGiven() {
        return getPrefix() + Util.color(this.wandGiven);
    }

    public int getUpdateInterval() {
        return this.updateInterval;
    }

    public List<String> getDepositMessages(int beacons, int sponge) {
        List<String> coloured = new ArrayList<>();
        for (String s : this.depositMessages) {
            s = s.replace("%beacons%", String.valueOf(beacons));
            s = s.replace("%sponge%", String.valueOf(sponge));
            coloured.add(Util.color(s));
        }
        return coloured;
    }

    public ItemStack getWand(int uses) {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta wandMeta = wand.getItemMeta();
        wandMeta.setDisplayName(Util.color(this.wandName));
        wandMeta.setLore(getWandLore(uses));
        wandMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS);
        wand.setItemMeta(wandMeta);
        wand = NBTEditor.set(wand, true,"beaconWand");
        wand = NBTEditor.set(wand, uses, "usesLeft");
        wand.addUnsafeEnchantment(Enchantment.DURABILITY, 999);
        return wand;
    }

    public List<String> getWandLore(int uses) {
        List<String> coloured = new ArrayList<>();
        for (String s : this.wandLore) {
            s = s.replace("%uses%", String.valueOf(uses));
            coloured.add(Util.color(s));
        }
        return coloured;
    }

    public String getCellTopName() {
        return Util.color(this.cellTopName);
    }



}
