package net.casheh.celllevel.commands;

import me.goodandevil.skyblock.api.island.IslandManager;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.inventory.DepositMenu;
import net.casheh.celllevel.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Deposit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return false;

        Player player = (Player) sender;

        if (IslandManager.hasIsland(player)) {
            DepositMenu menu = new DepositMenu(54, "Deposit Cell Value");
            player.openInventory(menu.getDepositInventory());
        } else {
            player.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou are not part of a cell!"));
        }

        return true;
    }

}
