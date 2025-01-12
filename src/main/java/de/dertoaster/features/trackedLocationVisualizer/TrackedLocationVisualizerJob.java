package de.dertoaster.features.trackedLocationVisualizer;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.SubCraft;
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
        final PlayerCraft playerCraft = CraftManager.getInstance().getCraftByPlayer(playerVisualizerDataEntry.getKey());
        if (playerCraft == null)
            return;
        final Set<Craft> craftSet = Set.of(playerCraft);

        for (Craft craft : CraftManager.getInstance().getCraftsInWorld(playerCraft.getWorld())) {
            if (craft == playerCraft) {
                continue;
            }
            if (craft instanceof SubCraft subCraft) {
                if (subCraft.getParent() == playerCraft) {
                    craftSet.add(craft);
                }
            }
        }

        craftSet.forEach(playerVisualizerDataEntry.getValue()::updateLocations);
    }

}
