package net.lightblockcrafting.listener;

import net.lightblockcrafting.util.LightBlockMarker;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Two jobs on every chunk load:
 * - Self-healing: if a light block was removed outside the normal break paths
 *   (e.g. by a piston, WorldEdit, or another plugin), orphaned marker entities
 *   would otherwise be left behind - this cleans them up.
 * - Re-registers rediscovered TextDisplay markers (e.g. after a server
 *   restart) with LightBlockMarker and syncs their visibility, since that
 *   registry only lives in memory.
 */
public class ChunkGuardListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof TextDisplay display && LightBlockMarker.isMarkerDisplay(display)) {
                LightBlockMarker.registerAndSync(display);
                continue;
            }

            if (!(entity instanceof Interaction interaction) || !LightBlockMarker.isMarkerInteraction(interaction)) {
                continue;
            }
            Block block = interaction.getLocation().getBlock();
            if (block.getType() != Material.LIGHT) {
                LightBlockMarker.removeAllAt(block.getLocation());
            }
        }
    }
}
