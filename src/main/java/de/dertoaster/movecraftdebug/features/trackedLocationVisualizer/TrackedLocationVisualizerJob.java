package de.dertoaster.movecraftdebug.features.trackedLocationVisualizer;

import de.dertoaster.movecraftdebug.CraftUtil;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class TrackedLocationVisualizerJob extends BukkitRunnable {

    // TODO: Change to UUID instead of player!
    static final WeakHashMap<UUID, VisualizerData> playerSettings = new WeakHashMap<>();

    public static VisualizerData getSettingsFor(final Player player) {
        return playerSettings.computeIfAbsent(player.getUniqueID(), p -> {
           return new VisualizerData(p.getUniqueID().);
        });
    }

    public static void remove(final Player player) {
        if (playerSettings.containsKey(player.getUniqueID())) {
            final UUID originalUUID = player.getUniqueID();
            playerSettings.get(new UUID(originalUUID.getMostSignificantBits(), originalUUID.getLeastSignificantBits())).resetAllHighlights();
        }
        playerSettings.remove(player.getUniqueID());
    }

    @Override
    public void run() {        
        playerSettings.entrySet().forEach(TrackedLocationVisualizerJob::update);
    }

    private static void update(Map.Entry<UUID, VisualizerData> playerVisualizerDataEntry) {
        // Ignore offline or null players
        Player playerObj = Bukkit.getPlayer(playerVisualizerDataEntry.getKey());
        if (playerObj == null || !playerObj.isOnline()) {
            // TODO: When the player is offline for too long, remove the mapping
            return;
        }
        
        playerVisualizerDataEntry.getValue().resetAllHighlights();
        final Set<Craft> craftSet = CraftUtil.getRelevantCrafts(playerVisualizerDataEntry.getKey());
        if (craftSet.isEmpty()) {
            return;
        }

        craftSet.forEach(playerVisualizerDataEntry.getValue()::updateLocations);
    }

}
