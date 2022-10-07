package it.myke.identity.utils.inventory;

import com.cryptomorin.xseries.XMaterial;
import it.myke.identity.utils.CustomHeads;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.config.CustomConfigsInit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryBuilder {
    private final String chars = "abcdefghijklmnopqrstuvwxyzA";
    @Getter private ArrayList<ActionElement> elements;
    @Getter private ItemStack filler;


    public InventoryBuilder getBuilder(CustomConfigsInit customConfigsInit, String inventory) {
        elements = new ArrayList<>();
        for (String s : customConfigsInit.getInventoriesConfig().getConfigurationSection(inventory + ".elements").getKeys(false)) {
            ConfigurationSection elementSection = customConfigsInit.getInventoriesConfig().getConfigurationSection(inventory + ".elements." + s);
            Action action = getActionOrDefault(elementSection);
            int intPosition = Integer.parseInt(elementSection.getName());
            char position = translate(Integer.parseInt(elementSection.getName()));
            elements.add(new ActionElement(intPosition ,position, action, parseStack(elementSection), elementSection.getString("name"), elementSection.getStringList("lore")));
        }
        String fillerPath = customConfigsInit.getInventoriesConfig().getString(inventory + ".filler");
        if(fillerPath != null) {
            filler = XMaterial.matchXMaterial(fillerPath).get().parseItem();
        }
        return this;
    }


    private ItemStack parseStack(ConfigurationSection elementSection) {
        if(!Objects.equals(elementSection.getString("material"), "CUSTOM_HEAD")) {
            ItemStack stack = XMaterial.matchXMaterial(elementSection.getString("material")).get().parseItem();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(elementSection.getString("name"));
            meta.setCustomModelData(elementSection.getInt("custom-model-data"));
            meta.setLore(elementSection.getStringList("lore"));
            stack.setItemMeta(meta);
            return stack;
        }

        if(elementSection.contains("texture")) {
            return CustomHeads.getCustomHead(elementSection.getString("texture"), FormatUtils.color(elementSection.getString("name")), FormatUtils.color(elementSection.getStringList("lore")));
        } else Bukkit.getLogger().severe("Texture value not found for " + elementSection.getName());

        Bukkit.getLogger().severe("Error while parsing inventory element: " + elementSection.getName() + " in inventory: " + elementSection.getParent().getName());
        return null;
    }


    private Action getActionOrDefault(ConfigurationSection elementSection) {
        if(elementSection.contains("action")) {
            return Action.valueOf(elementSection.getString("action").toUpperCase());
        }
        return Action.NONE;
    }

    public char translate(final int i) {
        return chars.charAt(i);
    }

}


