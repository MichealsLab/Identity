package it.myke.identity.listener;

import it.myke.identity.Identity;
import it.myke.identity.disk.Settings;
import it.myke.identity.inventories.Inventories;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static it.myke.identity.inventories.Inventories.inventories;

public class PlayerJoinEvent implements Listener {
    private Identity main;
    private PersonUtil personUtil;
    private CustomConfigsInit customConfigsInit;
    private PostProcessCommands postProcessCommands;
    private Inventories inventoryUtils;

    public PlayerJoinEvent(Identity identity, PostProcessCommands postProcessCommands, Inventories inventoryUtils, PersonUtil personUtil, CustomConfigsInit customConfigsInit) {
        if(!Settings.ONJOIN_MENU_ENABLED) return;
        identity.getServer().getPluginManager().registerEvents(this, identity);
        this.main = identity;
        this.personUtil = personUtil;
        this.customConfigsInit = customConfigsInit;
        this.postProcessCommands = postProcessCommands;
        this.inventoryUtils = inventoryUtils;
    }


    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(main, bukkitTask -> {
            Player player = event.getPlayer();
            if(personUtil.setup(player, customConfigsInit, main, postProcessCommands, inventoryUtils, false)) return;

            if (player.hasPermission("identity.showupdates") && !Settings.UPDATES_SHOWN) {
                if (!personUtil.isPerson(event.getPlayer().getUniqueId()))
                    inventories.get("update-" + main.getDescription().getVersion().replace(".", "-")).show(player);
            }

        }, (Settings.MENU_SHOW_DELAY/1000)*20);
    }

}
