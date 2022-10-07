package it.myke.identity.utils;


import it.myke.identity.inventories.InventoryType;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@AllArgsConstructor
public class GUIHolder implements InventoryHolder {
    InventoryType inventory;

    @Override
    public Inventory getInventory() {
        return null;
    }

    public InventoryType getEnum() {
        return inventory;
    }
}

