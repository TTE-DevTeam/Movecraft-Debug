package de.dertoaster.movecraftdebug.features.trackedLocationVisualizer;

import de.dertoaster.movecraftdebug.CraftUtil;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class TrackedLocationVisualizerJob extends BukkitRunnable {

    static final WeakHashMap<Player, VisualizerData> playerSettings = new WeakHashMap<>();

    public static VisualizerData getSettingsFor(final Player player) {
        return playerSettings.computeIfAbsent(player, p -> {
           return new VisualizerData(p);
        });
    }

    public static void remove(final Player player) {
        if (playerSettings.containsKey(player)) {
            playerSettings.get(player).resetAllHighlights();
        }
        playerSettings.remove(player);
    }

    @Override
    public void run() {
        playerSettings.entrySet().forEach(TrackedLocationVisualizerJob::update);
    }

    private static void update(Map.Entry<Player, VisualizerData> playerVisualizerDataEntry) {
        playerVisualizerDataEntry.getValue().resetAllHighlights();
        final Set<Craft> craftSet = CraftUtil.getRelevantCrafts(playerVisualizerDataEntry.getKey());
        if (craftSet.isEmpty()) {
            return;
        }

        craftSet.forEach(playerVisualizerDataEntry.getValue()::updateLocations);
    }

}
