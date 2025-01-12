package de.dertoaster.listener;

import de.dertoaster.HighlightUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        HighlightUtil.callOnJoin(event.getPlayer());
    }

}