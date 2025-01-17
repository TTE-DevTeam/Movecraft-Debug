package de.dertoaster.movecraftdebug.listener;

import de.dertoaster.movecraftdebug.init.MovecraftDebugTrackedLocations;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.events.CraftDetectEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class MovecraftListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCraftDetect(CraftDetectEvent event) {
        event.getCraft().getHitBox().forEach(movecraftLocation -> {
            Block block = movecraftLocation.toBukkit(event.getCraft().getWorld()).getBlock();
            if (block.getState() instanceof Dispenser) {
                event.getCraft().getTrackedLocations().computeIfAbsent(MovecraftDebugTrackedLocations.DISPENSERS, k -> new HashSet<>()).add(new TrackedLocation(event.getCraft(), movecraftLocation));
            }
            else if (block.getState() instanceof Sign) {
                event.getCraft().getTrackedLocations().computeIfAbsent(MovecraftDebugTrackedLocations.SIGNS, k -> new HashSet<>()).add(new TrackedLocation(event.getCraft(), movecraftLocation));
            }
        });
    }

}
