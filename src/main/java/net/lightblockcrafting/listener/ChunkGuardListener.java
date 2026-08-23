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
 * Selbstheilung: Wird ein Lichtblock ausserhalb der normalen Abbau-Wege entfernt
 * (z.B. durch Kolben, WorldEdit oder ein anderes Plugin), bleiben sonst verwaiste
 * Marker-Entities zurueck. Beim Laden eines Chunks wird das hier bereinigt.
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
