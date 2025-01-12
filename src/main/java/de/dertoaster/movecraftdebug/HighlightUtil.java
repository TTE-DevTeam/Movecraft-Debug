package de.dertoaster.movecraftdebug;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.google.common.primitives.Ints;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerEntityDestroy;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerEntityMetadata;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerScoreboardTeam;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * A set of utilities for highlighting block changes in the world
 */
public class HighlightUtil {

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        //disabled = Settings.CompatibilityMode || version.contains("17") || version.contains("18") || version.contains("19");
        // TODO: Fix support for 1.17+
    }
    private static final byte INVISIBLE = (byte) 0x20;
    private static final byte GLOWING = (byte) 0x40;
    private static final byte NOAI = (byte) 0x01;
    private static final int MOB_INDEX = 14;
    private static final int SLIME_INDEX = 15;

    public static int highlightBlockAt(Location location, Player player, final ChatColor color) {
        //if(disabled)
        //    return 0;
        if (!COLOR_TO_TEAM.containsKey(color)) {
            return 0;
        }
        var packet = new WrapperPlayServerSpawnEntityLiving();
        var id = new Random().nextInt();
        var uuid = UUID.randomUUID();
        packet.setX(location.getX() + .5);
        packet.setY(location.getY());
        packet.setZ(location.getZ() + .5);
        packet.setType(EntityType.MAGMA_CUBE);
        packet.setEntityID(id);
        packet.setUniqueId(uuid);

        // Create metadata
        var metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(id); // set id
        var watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
        watcher.setEntity(player); //Set the new data watcher's target
        watcher.setObject(0, Registry.get(Byte.class), (byte) (GLOWING ^ INVISIBLE)); //Set status to glowing and invisible
        watcher.setObject(MOB_INDEX, Registry.get(Byte.class), NOAI);
        var slimeData = new WrappedDataWatcherObject(SLIME_INDEX, Registry.get(Integer.class));
        watcher.setObject(slimeData, 2);
        metadata.setMetadata(watcher.getWatchableObjects());
//        var entity = packet.getEntity(location.getWorld());
//        entity.setGlowing(true);
//        if(!(entity instanceof LivingEntity)){
//            throw new IllegalStateException(entity + " must be magma cube, but was not.");
//        }
//        ((LivingEntity) entity).setInvisible(true);
        packet.sendPacket(player);
        metadata.sendPacket(player);
        addToTeam(uuid, color).sendPacket(player);
        return id;
    }

    public static void removeHighlights(Collection<Integer> ids, Player player){
        removeHighlights(Ints.toArray(ids), player);
    }

    public static void removeHighlights(int[] ids, Player player){
        //if(disabled)
        //    return;
        var packet = new WrapperPlayServerEntityDestroy();
        packet.setEntityIds(ids);
        packet.sendPacket(player);
    }

    static final Map<ChatColor, String> COLOR_TO_TEAM = Map.ofEntries(
            Map.entry(ChatColor.BLACK,        "mvcraft_hl_black"),
            Map.entry(ChatColor.DARK_BLUE,    "mvcraft_hl_dblue"),
            Map.entry(ChatColor.DARK_GREEN,   "mvcraft_hl_dgren"),
            Map.entry(ChatColor.DARK_AQUA,    "mvcraft_hl_daqua"),
            Map.entry(ChatColor.DARK_RED,     "mvcraft_hl_d_red"),
            Map.entry(ChatColor.DARK_PURPLE,  "mvcraft_hl_dpurp"),
            Map.entry(ChatColor.GOLD,         "mvcraft_hl_orang"),
            Map.entry(ChatColor.GRAY,         "mvcraft_hl__gray"),
            Map.entry(ChatColor.DARK_GRAY,    "mvcraft_hl_dgray"),
            Map.entry(ChatColor.BLUE,         "mvcraft_hl__blue"),
            Map.entry(ChatColor.GREEN,        "mvcraft_hl_green"),
            Map.entry(ChatColor.AQUA,         "mvcraft_hl__aqua"),
            Map.entry(ChatColor.RED,          "mvcraft_hl___red"),
            Map.entry(ChatColor.LIGHT_PURPLE, "mvcraft_hl_lpurp"),
            Map.entry(ChatColor.YELLOW,       "mvcraft_hl_yello"),
            Map.entry(ChatColor.WHITE,        "mvcraft_hl_white")
    );

    private static WrapperPlayServerScoreboardTeam createTeam(ChatColor color){
        final String name = COLOR_TO_TEAM.getOrDefault(color, null);
        if (name == null) {
            return null;
        }

        var packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(name);
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        packet.setDisplayName(WrappedChatComponent.fromText(""));
        packet.setNameTagVisibility("never");
        packet.setCollisionRule("never");
        packet.setPrefix(WrappedChatComponent.fromText("§"+ color.getChar()));
        packet.setSuffix(WrappedChatComponent.fromText(""));
        packet.setColor(color);
        return packet;
    }

    private static WrapperPlayServerScoreboardTeam addToTeam(UUID id, ChatColor teamColor) {
        String name = COLOR_TO_TEAM.getOrDefault(teamColor, null);
        if (name == null) {
            return null;
        }
        var packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(name);
        packet.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);
        packet.setPlayers(List.of(id.toString()));
        return packet;
    }

    public static void callOnJoin(final Player player) {
        //if(disabled)
        //    return;
        for (ChatColor color : COLOR_TO_TEAM.keySet()) {
            createTeam(color).sendPacket(player);
        }
    }
}
