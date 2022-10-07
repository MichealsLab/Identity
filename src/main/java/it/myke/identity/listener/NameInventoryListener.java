package it.myke.identity.listener;

import it.myke.identity.Identity;
import it.myke.identity.inventories.Inventories;
import it.myke.identity.inventories.InventoryType;
import it.myke.identity.inventories.InventoryManager;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.GUIHolder;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.ConfigLoader;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static it.myke.identity.inventories.Inventories.inventories;
import static it.myke.identity.inventories.Inventories.processStarted;


public class NameInventoryListener implements Listener {
    private final Identity main;
    private final PersonUtil personUtil;
    private final CustomConfigsInit customConfigsInit;
    private final PostProcessCommands postProcessCommands;
    private final Inventories inventoriesUtil;

    public NameInventoryListener(final Identity identity, final Inventories inventoriesUtil, final PersonUtil personUtil, final PostProcessCommands postProcessCommands, final CustomConfigsInit customConfigsInit) {
        identity.getServer().getPluginManager().registerEvents(this, identity);
        main = identity;
        this.personUtil = personUtil;
        this.postProcessCommands = postProcessCommands;
        this.customConfigsInit = customConfigsInit;
        this.inventoriesUtil = inventoriesUtil;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getHolder() == null) return;
        if (!event.getInventory().getHolder().equals(main.getNameHolder())) return;
        if(!personUtil.isPerson(event.getPlayer().getUniqueId())) return;
        if(!personUtil.getPerson(event.getPlayer().getUniqueId()).hasName()) return;
        if(processStarted.containsKey(event.getPlayer().getUniqueId())) return;

        main.getServer().getScheduler().runTaskLater(main, () -> inventories.get("name").show(event.getPlayer()),1L);
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(processStarted.containsKey(event.getPlayer().getUniqueId())) {
            String name;
            boolean setup = processStarted.get(event.getPlayer().getUniqueId());
            if(ConfigLoader.invName_onlyName) {
                name = event.getMessage().replace(" ", "");
                addName(event.getPlayer(), FormatUtils.firstUppercase(name));
                if(!setup) {
                    customConfigsInit.saveInConfig(event.getPlayer().getUniqueId(), personUtil);
                    event.getPlayer().sendMessage(ConfigLoader.message_modified_name);
                    event.getPlayer().closeInventory();
                    return;
                }
                //Opening nextInventory
                Bukkit.getScheduler().runTask(main, () -> new InventoryManager().openNextInventory(event.getPlayer(), main, personUtil, inventoriesUtil, postProcessCommands, customConfigsInit, setup));
                processStarted.remove(event.getPlayer().getUniqueId());
            } else {
                if(event.getMessage().split(" ").length == 2) {
                    String[] surname_and_name = event.getMessage().split(" ");
                    addName(event.getPlayer(), FormatUtils.firstUppercase(surname_and_name[0]) + " " + FormatUtils.firstUppercase(surname_and_name[1]));
                    if(!setup) {
                        customConfigsInit.saveInConfig(event.getPlayer().getUniqueId(), personUtil);
                        event.getPlayer().sendMessage(ConfigLoader.message_modified_name);
                        event.getPlayer().closeInventory();
                        return;
                    }
                    //Opening nextInventory
                    Bukkit.getScheduler().runTask(main, () -> new InventoryManager().openNextInventory(event.getPlayer(), main, personUtil, inventoriesUtil, postProcessCommands, customConfigsInit, setup));
                    processStarted.remove(event.getPlayer().getUniqueId());
                } else {
                    event.getPlayer().sendMessage(ConfigLoader.message_surname_needed);
                }
            }
            event.setCancelled(true);

        }
    }


    public void addName(Player player, String name) {
        personUtil.getPerson(player.getUniqueId()).setName(name);
    }






}
