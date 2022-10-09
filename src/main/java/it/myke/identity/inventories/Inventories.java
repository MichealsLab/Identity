package it.myke.identity.inventories;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.myke.identity.Identity;
import it.myke.identity.disk.Lang;
import it.myke.identity.disk.Settings;
import it.myke.identity.obj.Person;
import it.myke.identity.update.UpdateReader;
import it.myke.identity.update.UpdateShower;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.furnace.FurnaceElement;
import it.myke.identity.utils.furnace.FurnaceGui;
import it.myke.identity.utils.inventory.Action;
import it.myke.identity.utils.inventory.ActionElement;
import it.myke.identity.utils.inventory.InventoryBuilder;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Inventories extends AbstractInventories {
    public static HashMap<String, InventoryGui> inventories;
    public static HashMap<UUID, Boolean> processStarted = new HashMap<>();
    private HashMap<UUID, Long> cooldown;


    public Inventories(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        inventories = new HashMap<>();
        cooldown = new HashMap<>();
        if(Settings.NAME_ENABLED) inventories.put("name", getNameInventory(plugin, customConfigsInit, setup));
        if(Settings.GENDER_ENABLED) inventories.put("gender", getGenderChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup));
        if(Settings.AGE_ENABLED) inventories.put("age", getAgeChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup));

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
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "name");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getNameHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
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





    public static ItemStack getCustomStack() {
        ItemStack stack = XMaterial.matchXMaterial(Settings.ANVIL_GUI__ITEM__MATERIAL).parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Settings.ANVIL_GUI__ITEM__DISPLAY_NAME.decoration(TextDecoration.ITALIC, false));
        meta.lore(Settings.ANVIL_GUI__ITEM__LORE);
        meta.setCustomModelData(Settings.ANVIL_GUI__ITEM__CUSTOM_MODEL_DATA);
        stack.setItemMeta(meta);
        return stack;
    }




    private boolean getNameListener(Player player, InventoryGui inventoryGui, boolean setup) {
        processStarted.put(player.getUniqueId(), setup);
        inventoryGui.close();
        player.sendMessage(Lang.INSERT_NAME);
        if(Settings.NAME_TITLEBAR_ENABLED) {
            Title title = Title.title(
                    Lang.INSERT_NAME_TITLE,
                    Lang.INSERT_NAME_SUBTITLE,
                    Title.Times.of(Duration.ofMillis(1), Duration.ofMillis(70), Duration.ofMillis(1))
            );
            player.showTitle(title);
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
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "name");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getNameHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
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
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "gender");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getGenderHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        HashBasedTable<Action, String, Component> table = HashBasedTable.create();

        table.put(Action.FEMALE, Lang.FEMALE_GENDER, Lang.GENDER_FEMALE_SELECTED);
        table.put(Action.NONBINARY, Lang.NON_BINARY_GENDER, Lang.GENDER_NON_BINARY_SELECTED);
        table.put(Action.MALE, Lang.MALE_GENDER, Lang.GENDER_MALE_SELECTED);
        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(table.containsRow(actionElement.getAction())) {
                    Table.Cell<Action, String, Component> genderCell = table.cellSet().stream().filter(cell -> cell.getRowKey() == actionElement.getAction()).findFirst().get();
                    personUtil.getPerson(click.getWhoClicked().getUniqueId()).setGender(genderCell.getColumnKey());
                    click.getWhoClicked().sendMessage(genderCell.getValue());
                }
                if (!setup) {
                    customConfigsInit.saveInConfig(click.getWhoClicked().getUniqueId(), personUtil);
                    click.getWhoClicked().sendMessage(Lang.GENDER_EDITED);
                    click.getWhoClicked().closeInventory();
                    return true;
                }

                inventoryManager.openNextInventory((Player) click.getWhoClicked(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                return true;
            });
        }

        inventoryGui.setCloseAction(close -> {
            if(personUtil.isPerson(close.getPlayer().getUniqueId())) {
                Person person = personUtil.getPerson(close.getPlayer().getUniqueId());
                if(person.getGender() == null) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> inventoryGui.show(close.getPlayer()), 1);
                    return false;
                }
            }
            return false;
        });


        return inventoryGui;
    }



    @Override
    public InventoryGui getAgeChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "age");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getAgeHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
        inventoryGui.setFiller(inventoryBuilder.getFiller());
        int minAge = Settings.MIN_AGE;
        int maxAge = Settings.MAX_AGE;
        AtomicInteger actualAge = new AtomicInteger(minAge);

        for(ActionElement actionElement : inventoryBuilder.getElements()) {

            if (actionElement.getAction() == Action.CONFIRM_AGE) {
                inventoryGui.addElement(new DynamicGuiElement(actionElement.getCharPos(), (viewer) -> new StaticGuiElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                    personUtil.getPerson(click.getWhoClicked().getUniqueId()).setAge(actualAge.get());
                    click.getWhoClicked().sendMessage(Lang.AGE_CONFIRMED);
                    new InventoryManager().openNextInventory((Player) click.getWhoClicked(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    if(!setup) {
                        customConfigsInit.saveInConfig(click.getWhoClicked().getUniqueId(), personUtil);
                        click.getWhoClicked().sendMessage(Lang.AGE_EDITED);
                        click.getWhoClicked().closeInventory();
                    }
                    return true;
                },LegacyComponentSerializer.legacySection().serialize(actionElement.getDisplayname()).replace("%actualage%", String.valueOf(actualAge.get())),
                        FormatUtils.loreListToSingleString(Settings.translateComponent(actionElement.getLore())).replace("%actualage%", String.valueOf(actualAge.get())))));
            } else {

                inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {

                    switch (actionElement.getAction()) {
                        case REMOVE_AGE -> {
                            if (!(actualAge.get() - 1 < minAge)) {
                                actualAge.getAndDecrement();
                            } else {
                                click.getWhoClicked().sendActionBar(Lang.MIN_AGE_REACHED);
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                            click.getGui().draw();
                        }
                        case ADD_AGE -> {
                            if (!(actualAge.get() + 1 > maxAge)) {
                                actualAge.getAndIncrement();
                            } else {
                                click.getWhoClicked().sendActionBar(Lang.MAX_AGE_REACHED);
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                            click.getGui().draw();
                        }
                    }

                    return true;
                });
            }
        }

        inventoryGui.setCloseAction(close -> {
            if(personUtil.isPerson(close.getPlayer().getUniqueId())) {
                Person person = personUtil.getPerson(close.getPlayer().getUniqueId());
                if(person.getAge() == -1) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> inventoryGui.show(close.getPlayer()), 1);
                    return false;
                }
            }
            return false;
        });

        return inventoryGui;
    }





    @Override
    public FurnaceGui getAgeFurnaceGUI(Player player, Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        int minAge = Settings.MIN_AGE;
        int maxAge = Settings.MAX_AGE;
        AtomicInteger actualAge = new AtomicInteger(minAge);

        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "furnace.age");
        FurnaceGui furnaceGui = new FurnaceGui(inventoryBuilder.getTitle(), plugin, personUtil);

        for(ActionElement element : inventoryBuilder.getElements()) {
            switch (element.getAction()) {
                case REMOVE_AGE -> furnaceGui.addElement(new FurnaceElement(element.getIntPos(), false, -1, element.getStack(), click -> {
                    long now = System.currentTimeMillis();
                    long coolDownEnd = cooldown.getOrDefault(player.getUniqueId(), now);
                    if (now >= coolDownEnd) {
                        if (actualAge.get() - 1 >= minAge) {
                            actualAge.getAndDecrement();
                            furnaceGui.setActualAge(actualAge.get());

                            now = System.currentTimeMillis();
                            long cooldownMs = 200;
                            cooldown.put(player.getUniqueId(), now + cooldownMs);
                        } else {
                            click.getWhoClicked().sendActionBar(Lang.MIN_AGE_REACHED);
                            XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                        }
                    }
                }, element.getDisplayname(),
                        element.getLore()));
                case ADD_AGE -> furnaceGui.addElement(new FurnaceElement(element.getIntPos(), false, -1, element.getStack(), click -> {
                    long now = System.currentTimeMillis();
                    long coolDownEnd = cooldown.getOrDefault(player.getUniqueId(), now);
                    if (now >= coolDownEnd) {
                        if (actualAge.get() + 1 <= maxAge) {
                            actualAge.getAndIncrement();
                            furnaceGui.setActualAge(actualAge.get());

                            now = System.currentTimeMillis();
                            long cooldownMs = 200;
                            cooldown.put(player.getUniqueId(), now + cooldownMs);
                        } else {
                            click.getWhoClicked().sendActionBar(Lang.MAX_AGE_REACHED);
                            XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                        }
                    }
                }, element.getDisplayname(),
                        element.getLore()));
                case CONFIRM_AGE -> {
                    furnaceGui.setActualAge(actualAge.get());
                    ItemStack ageStack = furnaceGui.getCustomStack() != null ? furnaceGui.getCustomStack() : element.getStack();
                    furnaceGui.addElement(new FurnaceElement(element.getIntPos(), true, -1, ageStack, click -> {
                        personUtil.getPerson(player.getUniqueId()).setAge(actualAge.get());
                        furnaceGui.setCompleted(true);
                        if (!setup) {
                            customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                            player.sendMessage(Lang.AGE_EDITED);
                            player.closeInventory();
                            return;
                        }
                        InventoryManager inventoryManager = new InventoryManager();
                        inventoryManager.openNextInventory(player, plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    }, element.getDisplayname(),
                            element.getLore()));
                }
            }

        }
        return furnaceGui.build();
    }






    @Override
    public AnvilGUI.Builder getNameAnvilGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        return new AnvilGUI.Builder()
                .onComplete((player, text) -> {                                    //called when the inventory output slot is clicked
                    if(Settings.LASTNAME_REQUIRED) {
                        if(!text.contains(" ")) {
                            player.sendMessage(Lang.LASTNAME_REQUIRED);
                            return AnvilGUI.Response.text(LegacyComponentSerializer.legacySection().serialize(Lang.LASTNAME_REQUIRED));
                        } else {
                            String[] textSplit = text.trim().replaceAll(" +", " ").split(" ");
                            personUtil.getPerson(player.getUniqueId()).setName(FormatUtils.firstUppercase(textSplit[0]) + " " + FormatUtils.firstUppercase(textSplit[1]));
                            player.sendMessage(Lang.NAME_CONFIRMED.replaceText(TextReplacementConfig.builder()
                                    .matchLiteral("%name%").replacement(FormatUtils.firstUppercase(textSplit[0]) + " " + FormatUtils.firstUppercase(textSplit[1])).build()));
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
                        player.sendMessage(Lang.NAME_CONFIRMED.replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%name%").replacement(FormatUtils.firstUppercase(finalText)).build()));

                        return AnvilGUI.Response.close();
                    }
                })
                .onClose(player -> {
                    if (personUtil.getPerson(player.getUniqueId()).getName() != null) {
                        InventoryManager inventoryManager = new InventoryManager();
                        if(!setup) {
                            customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                            player.sendMessage(Lang.NAME_EDITED);
                            player.closeInventory();
                            return;
                        }
                        inventoryManager.openNextInventory(player, plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    }
                })
                .itemLeft(getCustomStack())
                .preventClose()
                .title(Settings.ANVIL_GUI__TITLE)
                .plugin(plugin);
    }






    @Override
    public void openGUI(InventoryType inventory, Settings.InventoryType type, Identity plugin, Player player, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        switch (inventory) {
            case NAME:
                if(type == Settings.InventoryType.ANVIL) {
                    this.getNameAnvilGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).open(player);
                }

                if (type == Settings.InventoryType.CHEST) {
                    this.getNameChestGUI(plugin, customConfigsInit, setup).show(player);
                }
                break;

            case GENDER:
                if (type == Settings.InventoryType.CHEST) {
                    this.getGenderChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }
                break;

            case AGE:
                if (type == Settings.InventoryType.FURNACE) {
                    this.getAgeFurnaceGUI(player, plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }

                if(type == Settings.InventoryType.CHEST) {
                    this.getAgeChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }
                break;
        }

    }
}
