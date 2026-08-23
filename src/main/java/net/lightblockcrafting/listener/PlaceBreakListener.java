package net.lightblockcrafting.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.lightblockcrafting.util.LightBlockItem;
import net.lightblockcrafting.util.LightBlockMarker;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

/**
 * Handles placing light blocks (including spawning the visible markers)
 * as well as every way a light block can be removed again.
 */
public class PlaceBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LIGHT) {
            return;
        }
        ItemStack itemInHand = event.getItemInHand();
        if (!LightBlockItem.isLightBlockItem(itemInHand)) {
            return;
        }

        int level = LightBlockItem.getLevel(itemInHand);
        BlockData data = block.getBlockData();
        if (data instanceof Levelled levelled) {
            levelled.setLevel(Math.min(level, levelled.getMaximumLevel()));
            block.setBlockData(levelled);
        }

        LightBlockMarker.spawn(block.getLocation(), level);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttackMarker(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Interaction interaction) || !LightBlockMarker.isMarkerInteraction(interaction)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        breakLightBlock(player, interaction);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractMarker(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction interaction) || !LightBlockMarker.isMarkerInteraction(interaction)) {
            return;
        }
        event.setCancelled(true);
        int level = LightBlockMarker.getLevel(interaction);
        event.getPlayer().sendActionBar(Component.text("Light Block - Level ", NamedTextColor.GRAY)
                .append(Component.text(level, NamedTextColor.AQUA)));
    }

    /**
     * Fallback: in case a light block is removed without going through the
     * interaction entity (e.g. the player holds a light block themselves,
     * another plugin or WorldEdit removes the block).
     */
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LIGHT || !LightBlockMarker.hasMarkerAt(block.getLocation())) {
            return;
        }

        event.setDropItems(false);
        int level = readLevel(block);
        LightBlockMarker.removeAllAt(block.getLocation());

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            dropItem(block, level);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        cleanupExploded(event.blockList().iterator());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        cleanupExploded(event.blockList().iterator());
    }

    private void cleanupExploded(Iterator<Block> blocks) {
        while (blocks.hasNext()) {
            Block block = blocks.next();
            if (block.getType() == Material.LIGHT && LightBlockMarker.hasMarkerAt(block.getLocation())) {
                LightBlockMarker.removeAllAt(block.getLocation());
            }
        }
    }

    private void breakLightBlock(Player player, Interaction interaction) {
        Block block = interaction.getLocation().getBlock();
        if (block.getType() != Material.LIGHT) {
            LightBlockMarker.removeAllAt(block.getLocation());
            return;
        }

        int level = LightBlockMarker.getLevel(interaction);
        World world = block.getWorld();
        world.playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);

        block.setType(Material.AIR);
        LightBlockMarker.removeAllAt(block.getLocation());

        if (player.getGameMode() != GameMode.CREATIVE) {
            dropItem(block, level);
        }
    }

    private void dropItem(Block block, int level) {
        ItemStack drop = LightBlockItem.create(level);
        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.1, 0.5), drop);
    }

    private int readLevel(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Levelled levelled) {
            return levelled.getLevel();
        }
        return LightBlockItem.MAX_LEVEL;
    }
}
