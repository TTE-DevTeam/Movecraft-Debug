package de.dertoaster.movecraftdebug;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.SubCraft;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CraftUtil {

    public static Set<Craft> getRelevantCrafts(UUID owner) {
        Player playerObj = Bukkit.getPlayer(owner);
        if (playerObj == null) {
            return Set.of();
        }
        // TODO: Add direct method to get craft by player UUID
        PlayerCraft playerCraft = CraftManager.getInstance().getCraftByPlayer(playerObj);
        if (playerCraft == null) {
            return Set.of();
        }
        Set<Craft> result = new HashSet<>();
        result.add(playerCraft);

        for (Craft craft : CraftManager.getInstance().getCraftsInWorld(playerCraft.getWorld())) {
            if (craft == playerCraft) {
                continue;
            }
            if (craft instanceof SubCraft subCraft) {
                if (subCraft.getParent() == playerCraft) {
                    result.add(craft);
                }
            }
        }

        return result;
    }

}
