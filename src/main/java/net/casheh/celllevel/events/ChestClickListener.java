package net.casheh.celllevel.events;

import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.managers.Wand;
import net.casheh.celllevel.nbt.NBTEditor;
import net.casheh.celllevel.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChestClickListener implements Listener {

    @EventHandler
    public void onChestClick(PlayerInteractEvent e) {

        if (e.getClickedBlock() == null)
            return;
        if (e.getPlayer().getInventory().getItemInMainHand() == null || e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
            return;
        if (!(e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST))
            return;

        if (NBTEditor.contains(e.getPlayer().getInventory().getItemInMainHand(), "beaconWand")) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);

                Bukkit.getScheduler().runTaskAsynchronously(CellLevel.inst, new Runnable() {
                    ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                    @Override
                    public void run() {
                        Wand wand = new Wand(item);
                        if (wand.usable()) {
                            CellLevel.inst.getTranslator().handleChestTranslate(e.getPlayer(), e.getClickedBlock());
                            wand.setUses(wand.getUses() - 1);
                            e.getPlayer().getInventory().setItemInMainHand(wand.getItem());
                        } else {
                            e.getPlayer().sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYour wand has ran out of charge!"));
                        }

//                        CellLevel.inst.getTranslator().handleChestTranslate(e.getPlayer(), e.getClickedBlock());



//                        if (NBTEditor.getInt(item, "usesLeft") > 0) {
//                            CellLevel.inst.getTranslator().handleChestTranslate(e.getPlayer(), e.getClickedBlock());
//                            item = NBTEditor.set(item, NBTEditor.getInt(item, "usesLeft") -1);
//                        } else {
//                            e.getPlayer().sendMessage(CellLevel.inst.getCfg().getPrefix() + Util.color("&cYour wand has run out of uses!"));
//                        }


                    }
                });
            }
        }
    }


}
