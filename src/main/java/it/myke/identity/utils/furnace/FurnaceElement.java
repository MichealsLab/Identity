package it.myke.identity.utils.furnace;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.util.List;

@Getter
@Setter
public class FurnaceElement {
    private Component displayname;
    private List<Component> lore;
    private int location;
    private Consumer<InventoryClickEvent> clickEvent;
    private ItemStack stack;
    private Material material;
    private boolean dynamic;
    private int customModelData;

    public FurnaceElement(int location, boolean dynamic, int customModelData, ItemStack stack, Consumer<InventoryClickEvent> clickEvent, final Component displayname, final List<Component> lore) {
        this.location = location;
        this.stack = stack;
        this.material = stack.getType();
        this.dynamic = dynamic;
        this.clickEvent = clickEvent;
        this.displayname = displayname;
        this.lore = lore;
        this.customModelData = customModelData;
    }

}
