package it.myke.identity.listener;

import it.myke.identity.Identity;
import it.myke.identity.utils.GUIHolder;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static it.myke.identity.inventories.Inventories.inventories;

public class GenderInventoryListener implements Listener {
    private final Identity main;
    private final PersonUtil personUtil;
    private final CustomConfigsInit customConfigsInit;
    private final PostProcessCommands postProcessCommands;

    public GenderInventoryListener(final Identity identity, final PersonUtil personUtil, final PostProcessCommands postProcessCommands, final CustomConfigsInit customConfigsInit) {
        main = identity;
        this.personUtil = personUtil;
        this.postProcessCommands = postProcessCommands;
        this.customConfigsInit = customConfigsInit;
        identity.getServer().getPluginManager().registerEvents(this, identity);

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(event.getInventory().getHolder() == null) return;
        if (!event.getInventory().getHolder().equals(main.getGenderHolder())) return;
        if(!personUtil.isPerson(event.getPlayer().getUniqueId())) return;
        if(personUtil.getPerson(event.getPlayer().getUniqueId()).hasGender()) return;
        main.getServer().getScheduler().runTaskLater(main, () -> inventories.get("gender").show(event.getPlayer()),1L);
    }



}
