package de.dertoaster.movecraftdebug;

import de.dertoaster.movecraftdebug.commands.VisualizerCommand;
import de.dertoaster.movecraftdebug.features.trackedLocationVisualizer.TrackedLocationVisualizerJob;
import de.dertoaster.movecraftdebug.init.MovecraftDebugCraftDataTags;
import de.dertoaster.movecraftdebug.init.MovecraftDebugCraftTypeProperties;
import de.dertoaster.movecraftdebug.listener.PlayerListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
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

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            VisualizerCommand.register(commands);
        });

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        MovecraftDebugCraftDataTags.register();

        new TrackedLocationVisualizerJob().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onLoad() {
        MovecraftDebugCraftTypeProperties.register();
    }
}