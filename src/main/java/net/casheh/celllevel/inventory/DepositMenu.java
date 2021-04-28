package net.casheh.celllevel.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class DepositMenu {

    private Inventory depositInventory;

    public DepositMenu(int size, String title) {
        this.depositInventory = Bukkit.createInventory(new DepositHolder(), size, title);
    }

    public Inventory getDepositInventory() {
        return this.depositInventory;
    }

}
