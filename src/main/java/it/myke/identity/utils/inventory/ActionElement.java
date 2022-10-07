package it.myke.identity.utils.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ActionElement {

    @Getter private final char charPos;
    @Getter private final int intPos;
    @Getter private final Action action;
    @Getter @Setter private ItemStack stack;
    @Getter private final String displayname;
    @Getter private final List<String> lore;


    public ActionElement(final int intPos, final char charPos, final Action action, final ItemStack stack, final String displayname, final List<String> lore) {
        this.charPos = charPos;
        this.action = action;
        this.stack = stack;
        this.displayname = displayname;
        this.lore = lore;
        this.intPos = intPos;
    }


}
