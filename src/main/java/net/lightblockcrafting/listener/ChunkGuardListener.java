package net.lightblockcrafting.listener;

import net.lightblockcrafting.util.LightBlockMarker;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Self-healing: if a light block is removed outside the normal break paths
 * (e.g. by a piston, WorldEdit, or another plugin), orphaned marker entities
 * would otherwise be left behind. This cleans them up when a chunk loads.
 */
public class ChunkGuardListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
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
