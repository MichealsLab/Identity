package it.myke.identity.utils.furnace;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@AllArgsConstructor
public class FurnaceHolder implements InventoryHolder {
    String inventory;

    @Override
    public Inventory getInventory() {
        return null;
    }
}
