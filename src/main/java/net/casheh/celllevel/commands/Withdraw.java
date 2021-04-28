package net.casheh.celllevel.commands;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.IslandManager;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.managers.IslandUtilities;
import net.casheh.celllevel.managers.PlayerUtilities;
import net.casheh.celllevel.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Withdraw implements CommandExecutor {

    IslandManager manager = SkyBlockAPI.getIslandManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return false;

        Player player = (Player) sender;

        if (!IslandManager.hasIsland(player)) {
            sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + ChatColor.RED + "You are not part of a cell!");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cInvalid usage. Correct usage: /withdrawlevels <beacon:sponge> <amount>"));
            return false;
        }

        if (args[0].equalsIgnoreCase("beacons") || args[0].equalsIgnoreCase("beacon")) {
            try {
                int amount = Integer.parseInt(args[1]);
                withdraw(player, amount, Material.BEACON);
            } catch (NumberFormatException ex) {
                sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("Invalid amount!"));
            }
        } else if (args[0].equalsIgnoreCase("sponge") || args[0].equalsIgnoreCase("sponges")) {
            try {
                int amount = Integer.parseInt(args[1]);
                withdraw(player, amount, Material.SPONGE);
            } catch (NumberFormatException ex) {
                sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("Invalid amount!"));
            }
        } else {
            sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cInvalid usage. Correct usage: /withdrawlevels <beacon:sponge> <amount>"));
        }
        return true;
    }

    private void withdraw(Player player, int amount, Material material) {
        PlayerUtilities playerUtilities = new PlayerUtilities(player.getUniqueId());
        IslandUtilities islandUtilities = new IslandUtilities(manager.getIsland(player));

        if (Util.getEmptySpace(player.getInventory(), material) < amount) {
            player.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou do not have enough inventory space!"));
            return;
        }


        switch (material) {
            case BEACON:
                if (!(playerUtilities.getBeacons() >= amount)) {
                    player.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou do not have enough beacons!"));
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(CellLevel.inst, new Runnable() {
                    @Override
                    public void run() {
                        playerUtilities.setBeacons(playerUtilities.getBeacons() - amount);
                        islandUtilities.setBeacons(islandUtilities.getBeacons() - amount);
                    }
                });
                break;
            case SPONGE:
                if (!(playerUtilities.getSponge() >= amount)) {
                    player.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou do not have enough sponges!"));
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(CellLevel.inst, new Runnable() {
                    @Override
                    public void run() {
                        playerUtilities.setSponge(playerUtilities.getSponge() - amount);
                        islandUtilities.setSponge(playerUtilities.getSponge() - amount);
                    }
                });
                break;
        }
        player.getInventory().addItem(new ItemStack(material, amount));
    }


}


