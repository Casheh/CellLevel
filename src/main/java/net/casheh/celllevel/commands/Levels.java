package net.casheh.celllevel.commands;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.IslandManager;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.inventory.LevelsMenu;
import net.casheh.celllevel.managers.IslandUtilities;
import net.casheh.celllevel.managers.PlayerUtilities;
import net.casheh.celllevel.util.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Levels implements CommandExecutor {

    private CellLevel plugin;

    private final HashMap<UUID, Confirmation> activeConfirms = new HashMap<>();

    public Levels(CellLevel plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof ConsoleCommandSender)
                return false;

            Player player = (Player) sender;

            if (!(IslandManager.hasIsland(player))) {
                player.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou are not in a cell!"));
                return false;
            }

            IslandManager manager = SkyBlockAPI.getIslandManager();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    LevelsMenu menu = new LevelsMenu(manager.getIsland(player));
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            menu.openMenu(player);
                        }
                    });
                }
            });
            return false;
        } else if (args[0].equalsIgnoreCase("wipe")) {
            if (sender instanceof ConsoleCommandSender) {
                wipe(sender);
                return false;
            }

            if (sender.hasPermission("celllevel.admin")) {
                this.activeConfirms.put(((Player) sender).getUniqueId(), Confirmation.WIPE);

                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        activeConfirms.remove(((Player) sender).getUniqueId());
                    }
                }, 300L);

                ComponentBuilder builder = new ComponentBuilder(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                       CellLevel.inst.getCfg().getPrefix()) + ChatColor.RED + "You have 15 seconds to confirm this action ");
                builder.bold(true);
                builder.append("[CLICK ME]");
                builder.color(net.md_5.bungee.api.ChatColor.GREEN);
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/levels confirm"));
                Player p = (Player) sender;
                p.spigot().sendMessage(builder.create());

            } else {
                sender.sendMessage(CellLevel.inst.getCfg().getNoPermission());
            }
        } else if (args[0].equalsIgnoreCase("confirm")) {
            if (sender instanceof ConsoleCommandSender)
                return false;

            Player player = (Player) sender;

            if (!this.activeConfirms.containsKey(player.getUniqueId())) {
                player.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou do not have anything to confirm!"));
                return false;
            }

            Confirmation confirmation = this.activeConfirms.get(player.getUniqueId());
            switch (confirmation) {
                case WIPE:
                    wipe(sender);
                    break;
                default:
            }
        } else if (args[0].equalsIgnoreCase("top")) {
            if (sender instanceof ConsoleCommandSender)
                return false;

            Player player = (Player) sender;
            player.openInventory(CellLevel.inst.getCellTop().getMenu());
        } else if (args[0].equalsIgnoreCase("givewand")) {
            if (sender instanceof ConsoleCommandSender || sender.hasPermission("celllevel.admin")) {
                if (!(args.length >= 3)) {
                    sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cInvalid syntax. Correct usage: /levels givewand <player> <uses>"));
                    return false;
                }
                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + ChatColor.RED + "That is not a valid online player!");
                    return false;
                }

                try {
                    ItemStack wand = plugin.getCfg().getWand(Integer.parseInt(args[2]));
                    player.getInventory().addItem(wand);
                    player.sendMessage(plugin.getCfg().getWandGiven());
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getCfg().getPrefix() + Util.color("&cThat is not a valid number!"));
                }

            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof ConsoleCommandSender || sender.hasPermission("celllevel.admin")) {
                CellLevel.inst.getCfg().assign();
                sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&aThe configuration files have been reloaded."));
            }
        } else if (args[0].equalsIgnoreCase("forceupdate") || args[0].equalsIgnoreCase("force")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getCellTop().updateTopList();
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            sender.sendMessage(plugin.getCfg().getPrefix() + Util.color("&aCell top menu updated."));
                        }
                    });
                }
            });
        } else if (args[0].equalsIgnoreCase("withdraw")) {
            if (sender instanceof ConsoleCommandSender)
                return false;

            Player player = (Player) sender;

            if (!IslandManager.hasIsland(player)) {
                sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYou are not in a cell."));
                return false;
            }

            if (args.length < 3) {
                sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cInvalid usage. Correct usage: /level withdraw <beacon:sponge> <amount>"));
                return false;
            }

            if (args[1].equalsIgnoreCase("beacons") || args[1].equalsIgnoreCase("beacon")) {

                try {
                    int amount = Integer.parseInt(args[2]);
                    withdraw(player, amount, Material.BEACON);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("Invalid amount!"));
                }


            } else if (args[1].equalsIgnoreCase("sponge") || args[1].equalsIgnoreCase("sponges")) {

                try {
                    int amount = Integer.parseInt(args[2]);
                    withdraw(player, amount, Material.SPONGE);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("Invalid amount!"));
                }

            } else {
                sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cInvalid usage. Correct usage: /level withdraw <beacon:sponge> <amount>"));
            }

        } else {
            sender.sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cInvalid command!"));
        }

        return true;
    }

    private void wipe(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getDatabase().wipeTable("players");
                plugin.getDatabase().wipeTable("virtual");

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        sender.sendMessage(plugin.getCfg().getPrefix() + ChatColor.GREEN + "Cell level successfully reset!");
                    }
                });
            }
        });
    }

    private void withdraw(Player player, int amount, Material material) {
        IslandManager manager = SkyBlockAPI.getIslandManager();
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

    private enum Confirmation {
        WIPE;
    }

}
