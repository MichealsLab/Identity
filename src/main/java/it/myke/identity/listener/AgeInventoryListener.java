package it.myke.identity.listener;

import it.myke.identity.Identity;
import it.myke.identity.inventories.InventoryType;
import it.myke.identity.utils.GUIHolder;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static it.myke.identity.inventories.Inventories.inventories;

public class AgeInventoryListener implements Listener {
    private final Identity main;
    private final PersonUtil personUtil;
    private final CustomConfigsInit customConfigsInit;
    private final PostProcessCommands postProcessCommands;

    public AgeInventoryListener(Identity identity, PersonUtil personUtil, PostProcessCommands postProcessCommands, CustomConfigsInit customConfigsInit) {
        main = identity;
        main.getServer().getPluginManager().registerEvents(this, identity);
        this.personUtil = personUtil;
        this.customConfigsInit = customConfigsInit;
        this.postProcessCommands = postProcessCommands;
    }


        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();
            if(event.getInventory().getHolder() == null) return;
            if (!event.getInventory().getHolder().equals(new GUIHolder(InventoryType.AGE))) return;
            if(!personUtil.isPerson(player.getUniqueId())) return;
            if(!personUtil.getPerson(player.getUniqueId()).hasAge()) return;
            if(event.getInventory().getHolder().equals(new GUIHolder(InventoryType.AGE))) {
                main.getServer().getScheduler().runTaskLater(main, () -> inventories.get("age").show(player), 1L);
            }
        }



    }





