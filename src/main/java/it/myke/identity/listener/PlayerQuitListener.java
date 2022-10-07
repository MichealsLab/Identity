package it.myke.identity.listener;

import it.myke.identity.Identity;
import it.myke.identity.utils.PersonUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final Identity main;
    private final PersonUtil personUtil;

    public PlayerQuitListener(final Identity identity, final PersonUtil personUtil) {
        this.main = identity;
        this.personUtil = personUtil;
        identity.getServer().getPluginManager().registerEvents(this, identity);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        personUtil.removePerson(event.getPlayer().getUniqueId());
    }


}
