package net.casheh.celllevel.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandManager;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.managers.IslandUtilities;
import net.casheh.celllevel.util.Skull;
import net.casheh.celllevel.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

public class CellTopMenu {

    private CellLevel plugin;

    public CellTopMenu(CellLevel plugin) {
        this.plugin = plugin;
    }

    private final Inventory cellTopMenu = Bukkit.createInventory(new LevelsHolder(), 54, CellLevel.inst.getCfg().getCellTopName());

    public void updateTopList() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        updateTopInventory(getTopIslands());
                    }
                });
            }
        });
    }

    private void updateTopInventory(List<Island> islands) {
        int[] slots = new int[] { 13, 21, 23, 29, 31, 33, 37, 39, 41, 43 };
        for (int i = 0; i < islands.size(); i++) {
            if (islands.get(i) != null) {
                Island island = islands.get(i);
                IslandUtilities islandUtilities = new IslandUtilities(island);
                OfflinePlayer owner = Bukkit.getOfflinePlayer(island.getOwnerUUID());
                String level = Util.addCommas((int) (0.5 * islandUtilities.getBeacons()) + islandUtilities.getSponge());

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                meta.setDisplayName(Util.color("&b&lCELL TOP " + (i+1)));
                List<String> lore = new ArrayList<>(Arrays.asList(" ",
                        Util.color("&8&l» &7Owner:"),
                        Util.color("&c&l  " + owner.getName()),
                        " ",
                        Util.color("&8&l» &7Level:"),
                        Util.color("&a&l  " + level),
                        " ",
                        Util.color("&8&l» &7Members:")));

                if (islandUtilities.getAllMembers().size() > 1) {
                    for (UUID uuid : islandUtilities.getAllMembers()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        if (!player.getUniqueId().equals(island.getOwnerUUID())) {
                            lore.add(Util.color("&d&l  " + player.getName()));
                        }
                    }
                } else {
                    lore.add(Util.color("  &d&lNone"));
                }

                meta.setLore(lore);
                meta.setOwningPlayer(owner);
                skull.setItemMeta(meta);
                this.cellTopMenu.setItem(slots[i], skull);
            } else {
                ItemStack skull = Util.getSkull(Skull.EMPTY);
                ItemMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setDisplayName(Util.color("&b&lCELL TOP " + (i+1)));
                skull.setItemMeta(meta);
                this.cellTopMenu.setItem(slots[i], skull);
            }

            ItemStack book = new ItemStack(Material.BOOK);
            ItemMeta meta = book.getItemMeta();
            meta.setDisplayName(Util.color("&b&lCELL TOP LEADERBOARD"));
            meta.setLore(Arrays.asList(Util.color("&7This menu shows the top 10 cells with"),
                 Util.color("&7the most cell value to compete in"),
                 Util.color("&7our weekly cell top competition for PayPal or"),
                 Util.color("&7Buycraft payouts!")));
            book.setItemMeta(meta);

            for (int j = 0; j < cellTopMenu.getSize(); j++) {
                if (j > 0 && j < 10)
                    cellTopMenu.setItem(j, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
                else if (j % 9 == 0)
                    cellTopMenu.setItem(j, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
                else if (j >= 45 && j <= 53)
                    cellTopMenu.setItem(j, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
                else if ((j+1) % 9 == 0)
                    cellTopMenu.setItem(j, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
        }

            cellTopMenu.setItem(49, book);

        }
    }

    public Inventory getMenu() {
        return this.cellTopMenu;
    }

    private List<Island> getTopIslands() {
        IslandManager manager = SkyBlockAPI.getIslandManager();
        List<Island> topIslands = new ArrayList<>();
        try {

            String query = "SELECT * FROM virtual ORDER BY 0.5 * beacons + sponge DESC";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            ResultSet rs = statement.executeQuery();

            while (topIslands.size() < 10) {
                if (rs.next()) {
                    Island island = manager.getIslandByUUID(UUID.fromString(rs.getString("islandId")));
                    if (island != null)
                        topIslands.add(island);
                } else {
                    topIslands.add(null);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topIslands;
    }



}
