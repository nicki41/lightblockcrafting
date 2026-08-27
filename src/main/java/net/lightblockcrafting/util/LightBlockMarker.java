package net.lightblockcrafting.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the visible TextDisplay (shows the light level as a number) and the
 * clickable Interaction entity, which together form a light block. The
 * Interaction entity is always visible to everyone - it's what makes the
 * block breakable without holding anything. The TextDisplay number, however,
 * is only shown to players who are currently holding a light block
 * themselves, mirroring vanilla's own level-indicator behaviour.
 */
public final class LightBlockMarker {

    private static final Set<UUID> DISPLAY_MARKERS = ConcurrentHashMap.newKeySet();

    private static Plugin plugin;

    private LightBlockMarker() {
    }

    public static void init(Plugin owningPlugin) {
        plugin = owningPlugin;
    }

    /**
     * Spawns the display and click entities for a light block placed at blockLocation.
     */
    public static void spawn(Location blockLocation, int level) {
        var block = blockLocation.getBlock();
        Location center = block.getLocation().add(0.5, 0.35, 0.5);

        TextDisplay display = center.getWorld().spawn(center, TextDisplay.class, entity -> {
            entity.text(Component.text(String.valueOf(level), NamedTextColor.YELLOW));
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            entity.setSeeThrough(false);
            entity.setShadowed(false);
            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
            entity.setPersistent(true);
            // Hidden by default - only shown to players holding a light block, see applyVisibility().
            entity.setVisibleByDefault(false);
            entity.getPersistentDataContainer().set(LightBlockKeys.MARKER, PersistentDataType.STRING, "display");
        });
        registerAndSync(display);

        Interaction interaction = center.getWorld().spawn(center.clone().subtract(0, 0.35, 0), Interaction.class, entity -> {
            entity.setInteractionWidth(0.98f);
            entity.setInteractionHeight(0.98f);
            entity.setResponsive(false);
            entity.setPersistent(true);
            entity.getPersistentDataContainer().set(LightBlockKeys.MARKER, PersistentDataType.STRING, "interaction");
            entity.getPersistentDataContainer().set(LightBlockKeys.ENTITY_LEVEL, PersistentDataType.INTEGER, level);
        });

        interaction.getPersistentDataContainer().set(LightBlockKeys.PAIRED_DISPLAY, PersistentDataType.STRING, display.getUniqueId().toString());
    }

    public static boolean isMarkerInteraction(Entity entity) {
        return entity instanceof Interaction
                && entity.getPersistentDataContainer().has(LightBlockKeys.MARKER, PersistentDataType.STRING);
    }

    public static boolean isMarkerDisplay(Entity entity) {
        return entity instanceof TextDisplay
                && entity.getPersistentDataContainer().has(LightBlockKeys.MARKER, PersistentDataType.STRING);
    }

    public static int getLevel(Interaction interaction) {
        Integer level = interaction.getPersistentDataContainer().get(LightBlockKeys.ENTITY_LEVEL, PersistentDataType.INTEGER);
        return level == null ? LightBlockItem.MAX_LEVEL : level;
    }

    public static boolean isHoldingLightBlock(Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.LIGHT
                || player.getInventory().getItemInOffHand().getType() == Material.LIGHT;
    }

    /**
     * Shows or hides every currently known light-level number for one player,
     * based on whether they are holding a light block right now.
     */
    public static void applyVisibility(Player player) {
        boolean holding = isHoldingLightBlock(player);
        for (UUID id : DISPLAY_MARKERS) {
            Entity entity = plugin.getServer().getEntity(id);
            if (entity == null || !entity.getWorld().equals(player.getWorld())) {
                continue;
            }
            if (holding) {
                player.showEntity(plugin, entity);
            } else {
                player.hideEntity(plugin, entity);
            }
        }
    }

    /**
     * Registers a (re)discovered display marker - e.g. one restored from a
     * freshly loaded chunk - and immediately syncs its visibility to every
     * online player in its world.
     */
    public static void registerAndSync(TextDisplay display) {
        DISPLAY_MARKERS.add(display.getUniqueId());
        for (Player player : display.getWorld().getPlayers()) {
            if (isHoldingLightBlock(player)) {
                player.showEntity(plugin, display);
            } else {
                player.hideEntity(plugin, display);
            }
        }
    }

    /**
     * Removes the TextDisplay entity paired with an interaction entity as well as the interaction itself.
     */
    public static void removePair(Interaction interaction) {
        String pairedId = interaction.getPersistentDataContainer().get(LightBlockKeys.PAIRED_DISPLAY, PersistentDataType.STRING);
        if (pairedId != null) {
            try {
                UUID uuid = UUID.fromString(pairedId);
                DISPLAY_MARKERS.remove(uuid);
                Entity paired = interaction.getServer().getEntity(uuid);
                if (paired != null) {
                    paired.remove();
                }
            } catch (IllegalArgumentException ignored) {
                // invalid UUID, nothing to do
            }
        }
        interaction.remove();
    }

    /**
     * Removes all marker entities (Interaction + TextDisplay) at exactly this block,
     * regardless of whether the pairing is still intact. Used for cleanup.
     */
    public static void removeAllAt(Location blockLocation) {
        Location center = blockLocation.getBlock().getLocation().add(0.5, 0.5, 0.5);
        Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, 0.6, 0.6, 0.6);
        for (Entity entity : nearby) {
            if (entity.getPersistentDataContainer().has(LightBlockKeys.MARKER, PersistentDataType.STRING)) {
                DISPLAY_MARKERS.remove(entity.getUniqueId());
                entity.remove();
            }
        }
    }

    public static boolean hasMarkerAt(Location blockLocation) {
        Location center = blockLocation.getBlock().getLocation().add(0.5, 0.5, 0.5);
        Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, 0.6, 0.6, 0.6);
        for (Entity entity : nearby) {
            if (entity.getPersistentDataContainer().has(LightBlockKeys.MARKER, PersistentDataType.STRING)) {
                return true;
            }
        }
        return false;
    }
}
