package de.dertoaster.movecraftdebug;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHelper {

    static final Scoreboard DUMMY_SCOREBOARD = new Scoreboard();


    public static void sendTeamToPlayer(NamedTextColor color, Player player) {
        final String name = HighlightUtil.COLOR_TO_TEAM.getOrDefault(color, null);
        if (name == null) {
            return;
        }

        PlayerTeam team = new PlayerTeam(DUMMY_SCOREBOARD, name);

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
        ((CraftPlayer)player).getHandle().connection.sendPacket(packet);
    }


}
