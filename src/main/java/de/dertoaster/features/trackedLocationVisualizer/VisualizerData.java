package de.dertoaster.features.trackedLocationVisualizer;

import de.dertoaster.HighlightUtil;
import net.countercraft.movecraft.TrackedLocation;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class VisualizerData {

    private final WeakReference<Player> playerReference;
    private final Map<NamespacedKey, ChatColor> backing = new WeakHashMap<>();
    private final Map<NamespacedKey, Set<Integer>> highlightEntityIDs = new WeakHashMap<>();

    public VisualizerData(final Player player) {
        this.playerReference = new WeakReference<Player>(player);
    }

    public void enable(final NamespacedKey key, boolean value) {
        if (value) {
            this.backing.putIfAbsent(key, ChatColor.WHITE);
            this.highlightEntityIDs.putIfAbsent(key, Set.of());
        } else {
            this.backing.remove(key);
            this.highlightEntityIDs.remove(key);
        }
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
        for (NamespacedKey key : highlightEntityIDs.keySet()) {
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

        for (TrackedLocation trackedLocation : craft.getTrackedLocations().getOrDefault(key, Set.of())) {
            int entityID = HighlightUtil.highlightBlockAt(trackedLocation.getAbsoluteLocation().toBukkit(craft.getWorld()), this.playerReference.get(), color);
            if (entityID == 0) {
                continue;
            }
            highlightEntityIDs.computeIfAbsent(key, k -> Set.of()).add(entityID);
        }
    }

    protected void resetHighlights(NamespacedKey key) {
        if (this.playerReference.get() == null) {
            return;
        }
        if (highlightEntityIDs.containsKey(key)) {
            HighlightUtil.removeHighlights(highlightEntityIDs.get(key), playerReference.get());
            highlightEntityIDs.get(key).clear();
        }
    }

}
