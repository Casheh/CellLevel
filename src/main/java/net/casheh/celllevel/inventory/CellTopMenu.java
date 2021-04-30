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
        for (int i = 0; i < 10; i++) {
            if (islands.get(i) != null) {
                Island island = islands.get(i);
                IslandUtilities islandUtilities = new IslandUtilities(island);
                OfflinePlayer owner = Bukkit.getOfflinePlayer(island.getOwnerUUID());
                String level = NumberFormat.getIntegerInstance().format((int) (0.5 * islandUtilities.getBeacons()) + islandUtilities.getSponge());

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "CELL TOP " + (i+1));
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
                        if (!player.getUniqueId().equals(island.getOwnerUUID()))
                            lore.add(Util.color("&d&l  " + player.getName()));
                    }
                } else {
                    lore.add(Util.color("&d&l0"));
                }

                meta.setLore(lore);
                meta.setOwningPlayer(owner);
                skull.setItemMeta(meta);

                this.cellTopMenu.setItem(getMatchingSlot(i), skull);

            } else {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "CELL TOP " + (i+1));

                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", Skull.EMPTY.getTexture()));

                try {
                    Field profileField = meta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(meta, profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                skull.setItemMeta(meta);

                this.cellTopMenu.setItem(getMatchingSlot(i), skull);
            }
        }

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(Util.color("&b&lCELL TOP LEADERBOARD"));
        meta.setLore(Arrays.asList(Util.color("&7This menu shows the top 10 cells with"),
                 Util.color("&7the most cell value to compete in"),
                 Util.color("&7our weekly cell top competition for PayPal or"),
                 Util.color("&7Buycraft payouts!")));
        book.setItemMeta(meta);

        for (int i = 0; i < cellTopMenu.getSize(); i++) {
            if (i > 0 && i < 10)
                cellTopMenu.setItem(i, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
            else if (i % 9 == 0)
                cellTopMenu.setItem(i, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
            else if (i >= 45 && i <= 53)
                cellTopMenu.setItem(i, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
            else if ((i+1) % 9 == 0)
                cellTopMenu.setItem(i, Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));
        }

        cellTopMenu.setItem(49, book);
    }

    public Inventory getMenu() {
        return this.cellTopMenu;
    }

    private int getMatchingSlot(int i) {
        switch (i) {
            case 0:
                return 13;
            case 1:
                return 21;
            case 2:
                return 23;
            case 3:
                return 29;
            case 4:
                return 31;
            case 5:
                return 33;
            case 6:
                return 37;
            case 7:
                return 39;
            case 8:
                return 41;
            case 9:
                return 43;

        }
        return -1;
    }

    private List<Island> getTopIslands() {
        IslandManager manager = SkyBlockAPI.getIslandManager();
        List<Island> topIslands = new ArrayList<>();
        try {

            String query = "SELECT * FROM virtual ORDER BY 0.5 * beacons + sponge DESC LIMIT 10";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            ResultSet rs = statement.executeQuery();

            for (int i = 0; i < 10; i++) {
                if (rs.next())
                    topIslands.add(manager.getIslandByUUID(UUID.fromString(rs.getString("islandId"))));
                else
                    topIslands.add(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topIslands;
    }



}
