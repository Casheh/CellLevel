package net.casheh.celllevel.managers;

import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.nbt.NBTEditor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Wand {

    private ItemStack item;

    public Wand(ItemStack item) {
        this.item = item;
    }

    public void setUses(int uses) {
        this.item = NBTEditor.set(item, uses, "usesLeft");
        ItemMeta meta = this.item.getItemMeta();
        meta.setLore(CellLevel.inst.getCfg().getWandLore(uses));
        this.item.setItemMeta(meta);
    }

    public int getUses() {
        if (NBTEditor.contains(this.item, "usesLeft"))
            return NBTEditor.getInt(this.item, "usesLeft");
        return 0;
    }

    public boolean usable() {
        return this.getUses() > 0;
    }

    public ItemStack getItem() {
        return this.item;
    }

}
