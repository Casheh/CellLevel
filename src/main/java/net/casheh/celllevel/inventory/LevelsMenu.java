package net.casheh.celllevel.inventory;

import me.goodandevil.skyblock.api.island.Island;
import net.casheh.celllevel.managers.IslandUtilities;
import net.casheh.celllevel.util.Skull;
import net.casheh.celllevel.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

public class LevelsMenu {

    private Island island;

    private Inventory levelsMenu;

    public LevelsMenu(Island island) {
        this.island = island;
        this.levelsMenu = Bukkit.createInventory(new LevelsHolder(), InventoryType.DROPPER, "Cell Level");

        for (int i = 0; i < levelsMenu.getSize(); i++) {
            if (i != 4) {
                ItemStack item = Util.removeAllText(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9));
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 999);
                this.levelsMenu.setItem(i, item);
            } else {
                ItemStack skull = Util.getSkull(Skull.LEVEL);

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                IslandUtilities islandUtilities = new IslandUtilities(this.island);
                meta.setLore(islandUtilities.getLevelsList());
                meta.setDisplayName(Util.color("&a&lCell Level"));

                skull.setItemMeta(meta);
                this.levelsMenu.setItem(i, skull);
            }
        }
    }

    public void openMenu(Player player) {
        player.openInventory(this.levelsMenu);
    }

}
