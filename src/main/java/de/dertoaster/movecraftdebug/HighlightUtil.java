package de.dertoaster.movecraftdebug;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedTeamParameters;
import com.google.common.primitives.Ints;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerEntityDestroy;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerEntityMetadata;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerScoreboardTeam;
import de.dertoaster.movecraftdebug.packets.WrapperPlayServerSpawnEntity;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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

    public static int highlightBlockAt(Location location, Player player, final NamedTextColor color) {
        //if(disabled)
        //    return 0;
        if (!COLOR_TO_TEAM.containsKey(color)) {
            return 0;
        }
        int id = PacketHelper.sendCreateLivingEntity(location, color, player);
        return id;
    }

    public static void removeHighlights(Collection<Integer> ids, Player player){
        removeHighlights(Ints.toArray(ids), player);
    }

    public static void removeHighlights(int[] ids, Player player){
        //if(disabled)
        //    return;
        PacketHelper.sendRemoveEntity(ids, player);
    }

    static final Map<NamedTextColor, String> COLOR_TO_TEAM = Map.ofEntries(
            Map.entry(NamedTextColor.BLACK,        "mvcraft_hl_black"),
            Map.entry(NamedTextColor.DARK_BLUE,    "mvcraft_hl_dblue"),
            Map.entry(NamedTextColor.DARK_GREEN,   "mvcraft_hl_dgren"),
            Map.entry(NamedTextColor.DARK_AQUA,    "mvcraft_hl_daqua"),
            Map.entry(NamedTextColor.DARK_RED,     "mvcraft_hl_d_red"),
            Map.entry(NamedTextColor.DARK_PURPLE,  "mvcraft_hl_dpurp"),
            Map.entry(NamedTextColor.GOLD,         "mvcraft_hl_orang"),
            Map.entry(NamedTextColor.GRAY,         "mvcraft_hl__gray"),
            Map.entry(NamedTextColor.DARK_GRAY,    "mvcraft_hl_dgray"),
            Map.entry(NamedTextColor.BLUE,         "mvcraft_hl__blue"),
            Map.entry(NamedTextColor.GREEN,        "mvcraft_hl_green"),
            Map.entry(NamedTextColor.AQUA,         "mvcraft_hl__aqua"),
            Map.entry(NamedTextColor.RED,          "mvcraft_hl___red"),
            Map.entry(NamedTextColor.LIGHT_PURPLE, "mvcraft_hl_lpurp"),
            Map.entry(NamedTextColor.YELLOW,       "mvcraft_hl_yello"),
            Map.entry(NamedTextColor.WHITE,        "mvcraft_hl_white")
    );

    private static WrapperPlayServerScoreboardTeam addToTeam(UUID id, NamedTextColor teamColor) {
        String name = COLOR_TO_TEAM.getOrDefault(teamColor, null);
        if (name == null) {
            return null;
        }
        var packet = new WrapperPlayServerScoreboardTeam();
        packet.setName(name);
        packet.setMethod(WrapperPlayServerScoreboardTeam.Method.ADD_PLAYER.ordinal());
        packet.setPlayers(List.of(id.toString()));
        return packet;
    }

    public static void callOnJoin(final Player player) {
        //if(disabled)
        //    return;
        for (NamedTextColor color : NamedTextColor.NAMES.values()) {
            PacketHelper.sendTeamToPlayer(color, player);
        }
    }

}
