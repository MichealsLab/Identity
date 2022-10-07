package it.myke.identity.utils.furnace;

import com.cryptomorin.xseries.XMaterial;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
@Setter
public class FurnaceGui implements Listener {
    private ArrayList<FurnaceElement> elements;
    private String title;
    private Inventory inventory;
    private JavaPlugin plugin;
    private int actualAge;
    private InventoryHolder holder;
    private PersonUtil personUtil;
    private boolean completed;

    public FurnaceGui(String title, JavaPlugin plugin, PersonUtil personUtil) {
        this.elements = new ArrayList<>();
        this.title = title;
        this.plugin = plugin;
        this.actualAge = ConfigLoader.min_age;
        this.personUtil = personUtil;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addElement(FurnaceElement element) {
        this.elements.add(element);
    }

    public void setElement(FurnaceElement element) {
        this.elements.set(element.getLocation(), element);
    }

    public void removeElement(FurnaceElement element) {
        this.elements.remove(element);
    }

    public FurnaceGui build() {
        this.holder = new FurnaceHolder("smartFurnace");

        Inventory inventory = Bukkit.createInventory(holder, InventoryType.FURNACE, title);
        for(int i = 0; i < elements.size(); i++) {
            inventory.setItem(i, edit(elements.get(i).getStack(), elements.get(i).getCustomModelData(), Arrays.stream(elements.get(i).getText()).map(s -> s.replace("%actualage%", String.valueOf(actualAge))).toArray(String[]::new)));
        }
        this.inventory = inventory;
        return this;
    }


    public void show(Player player) {
        player.openInventory(inventory);
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(event.getInventory().getHolder() == null) return;
        if(!event.getInventory().getHolder().equals(holder)) return;
        if(!personUtil.isPerson(event.getPlayer().getUniqueId())) return;
        if(completed) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> show((Player) event.getPlayer()), 1L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getHolder() == null) return;
        if(!event.getClickedInventory().getHolder().equals(holder)) return;
        event.setCancelled(true);

        for(FurnaceElement element : elements) {
            if(element.getLocation() == event.getSlot()) {
                element.getClickEvent().accept(event);
            }

            if(element.isDynamic()) {
                ItemStack customStack = getCustomStack();
                ItemStack stack = customStack != null ? customStack : edit(element.getStack(), this.getCustomModelData(this.actualAge), element.getText());
                event.getClickedInventory().setItem(element.getLocation(), stack);
                ((Player) event.getWhoClicked()).updateInventory();
            }
        }
    }

    public int getCustomModelData(int age) {
        if(plugin.getConfig().getConfigurationSection("inventories.age.data.years") != null) {
            ConfigurationSection years = plugin.getConfig().getConfigurationSection("inventories.age.data.years");
            if(years.isConfigurationSection(String.valueOf(age))) {
                return years.getInt(age + ".custom-model-data");
            }
        }
        return -1;
    }

    public ItemStack getCustomStack() {
        if(plugin.getConfig().getConfigurationSection("inventories.age.data.years") != null) {
            ConfigurationSection years = plugin.getConfig().getConfigurationSection("inventories.age.data.years");
            if(years.isConfigurationSection(String.valueOf(actualAge))) {
                ItemStack stack = XMaterial.matchXMaterial(years.getString(actualAge + ".material")).get().parseItem();
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(FormatUtils.color(years.getString(actualAge + ".name").replace("%actualage%", String.valueOf(actualAge))));
                List<String> lore = FormatUtils.replaceList(years.getStringList(actualAge + ".lore"), String.valueOf(actualAge), "%actualage%");
                meta.setLore(FormatUtils.color(lore));
                meta.setCustomModelData(years.getInt(actualAge + ".custom-model-data"));
                stack.setItemMeta(meta);
                return stack;
            }
        }
        return null;
    }


    private ItemStack edit(ItemStack itemStack, int customModelData, String... data) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(FormatUtils.color(data[0].replace("%actualage%", String.valueOf(actualAge))));
        if(data.length > 1) {
            itemMeta.setLore(Collections.singletonList(FormatUtils.color(data[1].replace("%actualage%", String.valueOf(actualAge)))));
        }

        if(customModelData != -1) {
            itemMeta.setCustomModelData(customModelData);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }







}



