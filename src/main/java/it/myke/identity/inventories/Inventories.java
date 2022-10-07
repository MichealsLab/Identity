package it.myke.identity.inventories;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.myke.identity.Identity;
import it.myke.identity.update.UpdateReader;
import it.myke.identity.update.UpdateShower;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.GUIHolder;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.furnace.FurnaceElement;
import it.myke.identity.utils.furnace.FurnaceGui;
import it.myke.identity.utils.inventory.Action;
import it.myke.identity.utils.inventory.ActionElement;
import it.myke.identity.utils.inventory.InventoryBuilder;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Inventories extends AbstractInventories {
    public static HashMap<String, InventoryGui> inventories;
    public static HashMap<UUID, Boolean> processStarted = new HashMap<>();
    private HashMap<UUID, Long> cooldown;


    public Inventories(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        inventories = new HashMap<>();
        cooldown = new HashMap<>();
        if(ConfigLoader.name) inventories.put("name", getNameInventory(plugin, customConfigsInit, setup));
        if(ConfigLoader.gender) inventories.put("gender", getGenderChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup));
        if(ConfigLoader.age) inventories.put("age", getAgeChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup));

        ArrayList<String> registeredVersions = new UpdateReader().getVersions(plugin);

        if(registeredVersions != null) {
            int i = 0;
            for(String version : registeredVersions) {
                inventories.put("update-" + version, new UpdateShower().getNewUpdatesMenu(plugin, registeredVersions, i));
                i++;
            }
        }




    }

    public InventoryGui getNameInventory(Identity plugin, CustomConfigsInit customConfigsInit, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getNameHolder(), ConfigLoader.invName_name, rows);
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "name");
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(actionElement.getAction() == Action.ENTER_NAME)
                    getNameListener((Player) click.getWhoClicked(), inventoryGui, setup);
                return true;
            });
        }


        return inventoryGui;
    }





    public static ItemStack getCustomStack(Identity plugin) {
        if(plugin.getConfig().getConfigurationSection("inventories.name.anvil-gui.item") != null) {
            ConfigurationSection data = plugin.getConfig().getConfigurationSection("inventories.name.anvil-gui.item");
            ItemStack stack = XMaterial.matchXMaterial(data.getString("material")).get().parseItem();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(FormatUtils.color(data.getString("displayName")));
            List<String> lore = data.getStringList("lore");
            meta.setLore(FormatUtils.color(lore));
            meta.setCustomModelData(data.getInt("custom-model-data"));
            stack.setItemMeta(meta);
            return stack;
        }
        return null;
    }




    private boolean getNameListener(Player player, InventoryGui inventoryGui, boolean setup) {
        processStarted.put(player.getUniqueId(), setup);
        inventoryGui.close();
        player.sendMessage(ConfigLoader.message_HeadClicked_name_ChatMessage);
        if(ConfigLoader.invName_titleBar_enabled) {
            player.sendTitle(ConfigLoader.message_title_Title, ConfigLoader.message_title_Subtitle, 1, 70, 1);
        }
        return true;
    }













    @Override
    public InventoryGui getNameChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getNameHolder(), ConfigLoader.invName_name, rows);
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "name");
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(actionElement.getAction() == Action.ENTER_NAME)
                    getNameListener((Player) click.getWhoClicked(), inventoryGui, setup);
                return true;
            });
        }


        return inventoryGui;
    }




    @Override
    public InventoryGui getGenderChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryManager inventoryManager = new InventoryManager();
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getGenderHolder(), ConfigLoader.invGender_name, rows);
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "gender");
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        HashBasedTable<Action, String, String> table = HashBasedTable.create();

        table.put(Action.FEMALE, ConfigLoader.message_female, ConfigLoader.message_femaleClicked);
        table.put(Action.NONBINARY, ConfigLoader.message_non_binary, ConfigLoader.message_nonBinaryClicked);
        table.put(Action.MALE, ConfigLoader.message_male, ConfigLoader.message_maleClicked);

        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(table.containsRow(actionElement.getAction())) {
                    Table.Cell<Action, String, String> genderCell = table.cellSet().stream().filter(cell -> cell.getRowKey() == actionElement.getAction()).findFirst().get();
                    personUtil.getPerson(click.getWhoClicked().getUniqueId()).setGender(genderCell.getColumnKey());
                    click.getWhoClicked().sendMessage(genderCell.getValue());
                }
                if (!setup) {
                    customConfigsInit.saveInConfig(click.getWhoClicked().getUniqueId(), personUtil);
                    click.getWhoClicked().sendMessage(ConfigLoader.message_modified_gender);
                    click.getWhoClicked().closeInventory();
                    return true;
                }

                inventoryManager.openNextInventory((Player) click.getWhoClicked(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                return true;
            });
        }



        return inventoryGui;
    }



    @Override
    public InventoryGui getAgeChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getAgeHolder(), ConfigLoader.invAge_name, rows);
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "age");
        inventoryGui.setFiller(inventoryBuilder.getFiller());
        int minAge = ConfigLoader.min_age;
        AtomicInteger actualAge = new AtomicInteger(ConfigLoader.min_age);
        int maxAge = ConfigLoader.max_age;

        for(ActionElement actionElement : inventoryBuilder.getElements()) {

            if (actionElement.getAction() == Action.CONFIRM_AGE) {
                inventoryGui.addElement(new DynamicGuiElement(actionElement.getCharPos(), (viewer) -> new StaticGuiElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                    personUtil.getPerson(click.getWhoClicked().getUniqueId()).setAge(actualAge.get());
                    click.getWhoClicked().sendMessage(ConfigLoader.message_ageConfirmed);
                    new InventoryManager().openNextInventory((Player) click.getWhoClicked(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    if(!setup) {
                        customConfigsInit.saveInConfig(click.getWhoClicked().getUniqueId(), personUtil);
                        click.getWhoClicked().sendMessage(ConfigLoader.message_modified_age);
                        click.getWhoClicked().closeInventory();
                    }
                    return true;
                }, FormatUtils.color(FormatUtils.color(actionElement.getDisplayname().replace("%actualage%", String.valueOf(actualAge.get())))), FormatUtils.loreListToSingleString(FormatUtils.color(
                        FormatUtils.replaceList(actionElement.getLore(), "%actualage%", String.valueOf(actualAge.get())))))));
            } else {

                inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {

                    switch (actionElement.getAction()) {
                        case REMOVE_AGE:
                            if (!(actualAge.get() - 1 < minAge)) {
                                actualAge.getAndDecrement();
                            } else {
                                FormatUtils.sendActionbar((Player) click.getWhoClicked(), ConfigLoader.message_minAgeReached);
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                            click.getGui().draw();
                            break;

                        case ADD_AGE:
                            if (!(actualAge.get() + 1 > maxAge)) {
                                actualAge.getAndIncrement();
                            } else {
                                FormatUtils.sendActionbar((Player) click.getWhoClicked(), ConfigLoader.message_maxAgeReached);
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                            click.getGui().draw();
                            break;
                    }

                    return true;
                });
            }
        }
        return inventoryGui;
    }





    @Override
    public FurnaceGui getAgeFurnaceGUI(Player player, Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        int minAge = ConfigLoader.min_age;
        int maxAge = ConfigLoader.max_age;
        AtomicInteger actualAge = new AtomicInteger(ConfigLoader.min_age);

        FurnaceGui furnaceGui = new FurnaceGui(ConfigLoader.invAge_name, plugin, personUtil);
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "furnace.age");

        for(ActionElement element : inventoryBuilder.getElements()) {
            switch (element.getAction()) {
                case REMOVE_AGE:
                    furnaceGui.addElement(new FurnaceElement(element.getIntPos(), false, -1, element.getStack(), click -> {
                        long now = System.currentTimeMillis();
                        long coolDownEnd = cooldown.getOrDefault(player.getUniqueId(), now);
                        if(now >= coolDownEnd) {
                            if (actualAge.get() - 1 >= minAge) {
                                actualAge.getAndDecrement();
                                furnaceGui.setActualAge(actualAge.get());

                                now = System.currentTimeMillis();
                                long cooldownMs = 200;
                                cooldown.put(player.getUniqueId(), now + cooldownMs);
                            } else {
                                FormatUtils.sendActionbar((Player) click.getWhoClicked(), ConfigLoader.message_minAgeReached);
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                        }
                    }, FormatUtils.color(element.getDisplayname()), FormatUtils.loreListToSingleString(FormatUtils.color(element.getLore()))));
                    break;

                case ADD_AGE:
                    furnaceGui.addElement(new FurnaceElement(element.getIntPos(), false, -1, element.getStack(), click -> {
                        long now = System.currentTimeMillis();
                        long coolDownEnd = cooldown.getOrDefault(player.getUniqueId(), now);
                        if(now >= coolDownEnd) {
                            if (actualAge.get() + 1 <= maxAge) {
                                actualAge.getAndIncrement();
                                furnaceGui.setActualAge(actualAge.get());

                                now = System.currentTimeMillis();
                                long cooldownMs = 200;
                                cooldown.put(player.getUniqueId(), now + cooldownMs);
                            } else {
                                FormatUtils.sendActionbar((Player) click.getWhoClicked(), ConfigLoader.message_maxAgeReached);
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                        }
                    }, FormatUtils.color(element.getDisplayname()), FormatUtils.loreListToSingleString(FormatUtils.color(element.getLore()))));
                    break;

                case CONFIRM_AGE:
                    furnaceGui.setActualAge(actualAge.get());
                    ItemStack ageStack = furnaceGui.getCustomStack() != null ? furnaceGui.getCustomStack() : element.getStack();

                    furnaceGui.addElement(new FurnaceElement(element.getIntPos(), true, -1, ageStack, click -> {
                        personUtil.getPerson(player.getUniqueId()).setAge(actualAge.get());
                        furnaceGui.setCompleted(true);
                        if (!setup) {
                            customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                            player.sendMessage(ConfigLoader.message_modified_age);
                            player.closeInventory();
                            return;
                        }
                        InventoryManager inventoryManager = new InventoryManager();
                        inventoryManager.openNextInventory(player, plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    }, FormatUtils.color(element.getDisplayname()), FormatUtils.loreListToSingleString(FormatUtils.color(element.getLore()))));
                    break;
            }

        }
        return furnaceGui.build();
    }






    @Override
    public AnvilGUI.Builder getNameAnvilGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        return new AnvilGUI.Builder()
                .onComplete((player, text) -> {                                    //called when the inventory output slot is clicked
                    if(!ConfigLoader.invName_onlyName) {
                        if(!text.contains(" ")) {
                            player.sendMessage(ConfigLoader.message_surname_needed);
                            return AnvilGUI.Response.text(ConfigLoader.message_short_surname_needed);
                        } else {
                            String[] textSplit = text.trim().replaceAll(" +", " ").split(" ");
                            personUtil.getPerson(player.getUniqueId()).setName(FormatUtils.firstUppercase(textSplit[0]) + " " + FormatUtils.firstUppercase(textSplit[1]));
                            player.sendMessage(ConfigLoader.message_name_set.replace("%s", FormatUtils.firstUppercase(textSplit[0]) + " " + FormatUtils.firstUppercase(textSplit[1])));
                            return AnvilGUI.Response.close();
                        }
                    } else {
                        String finalText;

                        if(text.contains(" ")) {
                            String[] textSplit = text.split(" ");
                            finalText = FormatUtils.firstUppercase(textSplit[0]);
                        } else {
                            finalText = FormatUtils.firstUppercase(text);
                        }


                        personUtil.getPerson(player.getUniqueId()).setName(FormatUtils.firstUppercase(finalText));
                        player.sendMessage(ConfigLoader.message_name_set.replace("%s", FormatUtils.firstUppercase(finalText)));
                        return AnvilGUI.Response.close();
                    }
                })
                .onClose(player -> {
                    if (personUtil.getPerson(player.getUniqueId()).getName() != null) {
                        InventoryManager inventoryManager = new InventoryManager();
                        if(!setup) {
                            customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                            player.sendMessage(ConfigLoader.message_modified_name);
                            player.closeInventory();
                            return;
                        }
                        inventoryManager.openNextInventory(player, plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    }
                })
                .itemLeft(getCustomStack(plugin))
                .preventClose()
                .title(FormatUtils.color(ConfigLoader.message_insert_name))
                .plugin(plugin);
    }






    @Override
    public void openGUI(InventoryType inventory, ConfigLoader.InventoryTypes type, Identity plugin, Player player, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        switch (inventory) {
            case NAME:
                if(type == ConfigLoader.InventoryTypes.ANVIL) {
                    this.getNameAnvilGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).open(player);
                }

                if (type == ConfigLoader.InventoryTypes.CHEST) {
                    this.getNameChestGUI(plugin, customConfigsInit, setup).show(player);
                }
                break;

            case GENDER:
                if (type == ConfigLoader.InventoryTypes.CHEST) {
                    this.getGenderChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }
                break;

            case AGE:
                if (type == ConfigLoader.InventoryTypes.FURNACE) {
                    this.getAgeFurnaceGUI(player, plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }

                if(type == ConfigLoader.InventoryTypes.CHEST) {
                    this.getAgeChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }
                break;
        }

    }
}
