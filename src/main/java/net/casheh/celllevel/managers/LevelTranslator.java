package net.casheh.celllevel.managers;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandManager;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;


public class LevelTranslator {

    public void handleChestTranslate(Player player, Block clickedBlock) {
        IslandManager manager = SkyBlockAPI.getIslandManager();

        if (!IslandManager.hasIsland(player)) {
            player.sendMessage(CellLevel.inst.getCfg().getNotAtIsland());
            return;
        }
        if (!manager.getIsland(player).equals(manager.getIslandPlayerAt(player))) {
            player.sendMessage(CellLevel.inst.getCfg().getNotAtIsland());
            return;
        }

        Chest chest = (Chest) clickedBlock.getState();
        Island island = manager.getIsland(player);
        IslandUtilities islandUtilities = new IslandUtilities(island);
        PlayerUtilities playerUtilities = new PlayerUtilities(player.getUniqueId());

        int beacons = Util.getMaterialCount(Material.BEACON, chest.getInventory());
        int sponge = Util.getMaterialCount(Material.SPONGE, chest.getInventory());

        chest.getInventory().remove(Material.BEACON);
        chest.getInventory().remove(Material.SPONGE);

        player.sendMessage(ChatColor.AQUA  + "Beacons: " + beacons);
        player.sendMessage(ChatColor.GOLD + "Sponge: " + sponge);

        islandUtilities.setBeacons(islandUtilities.getBeacons() + beacons);
        islandUtilities.setSponge(islandUtilities.getSponge() + sponge);
        playerUtilities.setBeacons(playerUtilities.getBeacons() + beacons);
        playerUtilities.setSponge(playerUtilities.getSponge() + sponge);
    }


}
