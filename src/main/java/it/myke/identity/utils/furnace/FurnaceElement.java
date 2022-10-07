package it.myke.identity.utils.furnace;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

@Getter
@Setter
public class FurnaceElement {
    private String[] text;
    private int location;
    private Consumer<InventoryClickEvent> clickEvent;
    private ItemStack stack;
    private Material material;
    private boolean dynamic;
    private int customModelData;

    public FurnaceElement(int location, boolean dynamic, int customModelData, ItemStack stack, Consumer<InventoryClickEvent> clickEvent, String... text) {
        this.location = location;
        this.stack = stack;
        this.material = stack.getType();
        this.dynamic = dynamic;
        this.clickEvent = clickEvent;
        this.text = text;
        this.customModelData = customModelData;
    }

}
