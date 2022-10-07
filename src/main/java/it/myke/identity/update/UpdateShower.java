package it.myke.identity.update;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.myke.identity.Identity;
import it.myke.identity.utils.CustomHeads;
import it.myke.identity.utils.FormatUtils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

import static it.myke.identity.inventories.Inventories.inventories;

public class UpdateShower {
    private final String bugfix_headValue = "ff9d9de62ecae9b798555fd23e8ca35e2605291939c1862fe79066698c9508a7";
    private final String important_headValue = "4c4d5461361b99793ea1c0498b807d6613b7b2c46a7a77b3d6d602be92ace343";
    private final String feature_headValue = "a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6";



    public InventoryGui getNewUpdatesMenu(Identity identity, ArrayList<String> versions, int position) {
        String[] rows = {
                "         ",
                "dggggggge",
                "   b c   "
        };

        InventoryGui inventoryGui = new InventoryGui(identity, "Updates for version (" + versions.get(position).replace("-", ".") + ")", rows);
        inventoryGui.setFiller(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());

        GuiElementGroup group = new GuiElementGroup('g');

        for(Update update : new UpdateReader().getUpdates(identity, versions.get(position))) {
            group.addElement(new StaticGuiElement('g', getUpdateItemStack(update)));
        }
        group.setFiller(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseItem());
        inventoryGui.addElement(group);

        if(versions.size() > 0) {
            if (position - 1 != -1) {
                inventoryGui.addElement(new StaticGuiElement('d', XMaterial.REDSTONE.parseItem(), click -> {
                    click.getWhoClicked().closeInventory();
                    getInventoryByUpdateVersion(versions.get(position - 1)).show(click.getWhoClicked());
                    return true;
                }, FormatUtils.color("&#FF0048Go to previous update &7(" + versions.get(position - 1).replace("-", ".") + ")")));
            }

            if (!(position + 1 >= versions.size())) {
                inventoryGui.addElement(new StaticGuiElement('e', XMaterial.ARROW.parseItem(), click -> {
                    click.getWhoClicked().closeInventory();
                    getInventoryByUpdateVersion(versions.get(position + 1)).show(click.getWhoClicked());
                    return true;
                }
                        , FormatUtils.color("&#2FAEFFGo to next update &7(" + versions.get(position + 1).replace("-", ".") + ")")));
            }
        }




        inventoryGui.addElement(new GuiPageElement('b', XMaterial.GLOWSTONE_DUST.parseItem(), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
        inventoryGui.addElement(new GuiPageElement('c', XMaterial.ARROW.parseItem(), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

        inventoryGui.setCloseAction(close -> {
            if(!identity.getConfig().getBoolean("updates.menu-shown")) {
                identity.getConfig().set("updates.menu-shown", true);
                identity.saveConfig();
                close.getPlayer().sendMessage(FormatUtils.color("&#17C600Thank you for using Identity! Next Updates will be shown to you only if you want it by command, &7/identity showupdates&#2C3BFF."));
            }
            return true;
        });

        return inventoryGui;
    }



    private InventoryGui getInventoryByUpdateVersion(String updateVersion) {
        return inventories.get("update-" + updateVersion);
    }



    public ItemStack getUpdateItemStack(Update update) {
        switch (update.getUpdateType()) {
            case BUGFIX:
                return CustomHeads.getCustomHead(bugfix_headValue, FormatUtils.color("&#FF3131Bug-Fix"), Collections.singletonList(FormatUtils.color("&#FF3131&l* &7" + update.getUpdateShortDescription())));
            case IMPORTANT:
                return CustomHeads.getCustomHead(important_headValue, FormatUtils.color("&#D98A00Fix"), Collections.singletonList(FormatUtils.color("&#D98A00&l* &7" + update.getUpdateShortDescription())));
            case FEATURE:
                return CustomHeads.getCustomHead(feature_headValue, FormatUtils.color("&#18FF00Feature"), Collections.singletonList(FormatUtils.color("&#18FF00&l+ &7" + update.getUpdateShortDescription())));
        }
        return XMaterial.BARRIER.parseItem();
    }




}


class Update {

    @Getter
    private final UpdateType updateType;
    @Getter
    private final String updateShortDescription;

    public Update(final UpdateType updateType, final String updateShortDescription) {
        this.updateType = updateType;
        this.updateShortDescription = updateShortDescription;
    }

}


enum UpdateType {
    BUGFIX,
    IMPORTANT,
    FEATURE
}
