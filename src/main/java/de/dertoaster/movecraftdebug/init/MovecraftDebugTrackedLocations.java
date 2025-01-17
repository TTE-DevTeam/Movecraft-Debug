package de.dertoaster.movecraftdebug.init;

import de.dertoaster.movecraftdebug.MovecraftDebugPlugin;
import org.bukkit.NamespacedKey;

public class MovecraftDebugTrackedLocations {

    public static NamespacedKey DISPENSERS = new NamespacedKey(MovecraftDebugPlugin.getInstance(), "dispensers");
    public static NamespacedKey SIGNS = new NamespacedKey(MovecraftDebugPlugin.getInstance(), "signs");

    public static void register() {
        // Does not need to do anything
    }
}
