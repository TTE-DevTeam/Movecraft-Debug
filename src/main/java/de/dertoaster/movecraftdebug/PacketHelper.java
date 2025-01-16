package de.dertoaster.movecraftdebug;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.List;
import java.util.UUID;

public class PacketHelper {

    static final Scoreboard DUMMY_SCOREBOARD = new Scoreboard();


    static PlayerTeam getTeam(NamedTextColor color) {
        final String name = HighlightUtil.COLOR_TO_TEAM.getOrDefault(color, null);
        if (name == null) {
            return null;
        }

        PlayerTeam team = new PlayerTeam(DUMMY_SCOREBOARD, name);
        return team;
    }

    // Creates a fake team
    public static void sendTeamToPlayer(NamedTextColor color, Player receiver) {
        PlayerTeam team = getTeam(color);
        if (team == null) {
            return;
        }

        team.setColor(ChatFormatting.getByName(color.toString().toUpperCase()));
        team.setCollisionRule(Team.CollisionRule.NEVER);
        team.setSeeFriendlyInvisibles(true);
        team.setNameTagVisibility(Team.Visibility.ALWAYS);
        team.setAllowFriendlyFire(false);
        team.setDeathMessageVisibility(Team.Visibility.NEVER);
        team.setDisplayName(Component.empty());
        team.setPlayerPrefix(Component.empty());
        team.setPlayerSuffix(Component.empty());

        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
        sendPacket(receiver, packet);
    }

    // Adds a given entity by UUID to a given team and sends it
    public static void sendAddToTeam(UUID entity, NamedTextColor color, Player receiver) {
        PlayerTeam team = getTeam(color);
        if (team == null) {
            return;
        }

        team.getPlayers().add(entity.toString());

        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false);
        sendPacket(receiver, packet);
    }

    // Removes a entity on the client
    public static void sendRemoveEntity(int[] entities, Player receiver) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entities);
        sendPacket(receiver, packet);
    }

    // Create a fake entity on the client
    public static int sendCreateLivingEntity(Location location, NamedTextColor teamColor, Player receiver) {
        final int entityID = new Random().nextInt();
        final UUID uuid = UUID.randomUUID();

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                entityID,
                uuid,
                (double)location.getBlockX() + .5D,
                (double)location.getBlockY(),
                (double)location.getBlockZ() + .5D,
                0F,
                0F,
                EntityType.MAGMA_CUBE,
                0,
                Vec3.ZERO,
                0D
        );
        sendPacket(receiver, packet);

        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                entityID,
                List.of(
                        new SynchedEntityData.DataValue<Integer>(15, EntityDataSerializers.INT, 2)
                )

        );
        //sendPacket(receiver, dataPacket);

        sendAddToTeam(uuid, teamColor, receiver);
        return entityID;
    }

    static void sendPacket(Player receiver, Packet packet) {
        ((CraftPlayer)receiver).getHandle().connection.sendPacket(packet);
    }


}
