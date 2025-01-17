package de.dertoaster.movecraftdebug.features.trackedLocationVisualizer;

import de.dertoaster.movecraftdebug.HighlightUtil;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.*;

public class VisualizerData {

    private final WeakReference<Player> playerReference;
    private final Map<NamespacedKey, ChatColor> backing = new HashMap<>();
    private final Map<NamespacedKey, Set<Location>> highlightedBlocks = new HashMap<>();

    public VisualizerData(final Player player) {
        this.playerReference = new WeakReference<Player>(player);
    }

    public void disable(final NamespacedKey key) {
        this.resetHighlights(key);
        this.backing.remove(key);
        this.highlightedBlocks.remove(key);
    }

    public void enable(final NamespacedKey key, ChatColor color) {
        if (backing.containsKey(key)) {
            disable(key);
        }
        this.backing.putIfAbsent(key, color == null ? ChatColor.WHITE : color);
        this.highlightedBlocks.putIfAbsent(key, new HashSet<>());
    }

    public void setHighlightColor(final NamespacedKey key, ChatColor value) {
        if (backing.containsKey(key) && !backing.get(key).equals(value)) {
            backing.put(key, value);
        }
    }

    public void updateLocations(final Craft craft) {
        for (NamespacedKey key : craft.getTrackedLocations().keySet()) {
            sendHighlights(key, craft);
        }
    }

    public void resetAllHighlights() {
        for (NamespacedKey key : highlightedBlocks.keySet()) {
            resetHighlights(key);
        }
    }

    public void setHighlightColor(NamespacedKey key, ChatColor color, Craft craft) {
        if (this.backing.containsKey(key)) {
            resetHighlights(key);
        }
        this.backing.put(key, color);
        if (craft != null) {
            sendHighlights(key, craft);
        }
    }

    protected void sendHighlights(NamespacedKey key, Craft craft) {
        if (this.playerReference.get() == null) {
            return;
        }
        ChatColor color = backing.getOrDefault(key, null);
        if (color == null) {
            return;
        }

        try {
            for (TrackedLocation trackedLocation : craft.getTrackedLocations().getOrDefault(key, Set.of())) {
                final Location location = trackedLocation.getAbsoluteLocation().toBukkit(craft.getWorld());
                HighlightUtil.highlightBlockAt(location, this.playerReference.get(), color);
                highlightedBlocks.computeIfAbsent(key, k -> new HashSet<>()).add(location);
            }
        } catch(ReflectiveOperationException ex) {
            return;
        }
    }

    protected void resetHighlights(NamespacedKey key) {
        if (this.playerReference.get() == null) {
            return;
        }
        if (highlightedBlocks.containsKey(key)) {
            try {
                HighlightUtil.removeHighlights(highlightedBlocks.get(key), playerReference.get());
            } catch(ReflectiveOperationException ex) {
                // Ignore
            }
            highlightedBlocks.get(key).clear();
        }
    }

}
