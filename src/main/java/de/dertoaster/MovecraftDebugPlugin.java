package de.dertoaster;

import de.dertoaster.features.trackedLocationVisualizer.TrackedLocationVisualizerJob;
import de.dertoaster.init.MovecraftDebugCraftDataTags;
import de.dertoaster.init.MovecraftDebugCraftTypeProperties;
import org.bukkit.plugin.java.JavaPlugin;

public final class MovecraftDebugPlugin extends JavaPlugin {

    private static JavaPlugin INSTANCE;

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        super.onEnable();

        MovecraftDebugCraftDataTags.register();

        new TrackedLocationVisualizerJob().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onLoad() {
        MovecraftDebugCraftTypeProperties.register();
    }
}