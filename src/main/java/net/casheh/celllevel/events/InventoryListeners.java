package net.casheh.celllevel.events;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.IslandManager;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.inventory.DepositHolder;
import net.casheh.celllevel.inventory.LevelsHolder;
import net.casheh.celllevel.managers.IslandUtilities;
import net.casheh.celllevel.managers.PlayerUtilities;
import net.casheh.celllevel.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.hamcrest.core.Is;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().getHolder() instanceof LevelsHolder) {
            e.setCancelled(true);
            return;
        }

        if (e.getInventory() != null && e.getInventory().getHolder() instanceof DepositHolder) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (!(e.getCurrentItem().getType() == Material.BEACON || e.getCurrentItem().getType() == Material.SPONGE)) {
                if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || e.getAction() == InventoryAction.COLLECT_TO_CURSOR)
                    e.setCancelled(true);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory() != null && e.getInventory().getHolder() instanceof DepositHolder) {
            Player player = (Player) e.getPlayer();
            IslandManager manager = SkyBlockAPI.getIslandManager();
            PlayerUtilities playerUtilities = new PlayerUtilities(player.getUniqueId());

            int beacons = Util.getMaterialCount(Material.BEACON, e.getInventory());
            int sponge = Util.getMaterialCount(Material.SPONGE, e.getInventory());

            if (IslandManager.hasIsland(player)) {
                IslandUtilities islandUtilities = new IslandUtilities(manager.getIsland(player));

                Bukkit.getScheduler().runTaskAsynchronously(CellLevel.inst, new Runnable() {
                    @Override
                    public void run() {
                        islandUtilities.setBeacons(islandUtilities.getBeacons() + beacons);
                        islandUtilities.setSponge(islandUtilities.getSponge() + sponge);
                        playerUtilities.setBeacons(playerUtilities.getBeacons() + beacons);
                        playerUtilities.setSponge(playerUtilities.getSponge() + sponge);
                    }
                });

                if (!(beacons == 0 && sponge == 0)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                    for (String message : CellLevel.inst.getCfg().getDepositMessages(beacons, sponge)) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }




}
