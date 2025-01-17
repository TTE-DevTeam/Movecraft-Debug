package de.dertoaster.movecraftdebug;

import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * A set of utilities for highlighting block changes in the world
 */
public class HighlightUtil {

    static final GlowingBlocks GLOWING_BLOCKS = new GlowingBlocks(MovecraftDebugPlugin.getInstance());

    public static void highlightBlockAt(Location location, Player player, final ChatColor color) throws ReflectiveOperationException {
        GLOWING_BLOCKS.setGlowing(location, player, color);
    }

    public static void removeHighlights(Collection<Location> locations, Player player) throws ReflectiveOperationException {
        for (Location l : locations) {
            GLOWING_BLOCKS.unsetGlowing(l, player);
        }
    }

}
