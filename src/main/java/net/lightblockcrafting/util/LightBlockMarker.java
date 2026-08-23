package net.lightblockcrafting.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.UUID;

/**
 * Verwaltet die sichtbare TextDisplay- (zeigt die Lichtstufe als Zahl) und die
 * klickbare Interaction-Entity, die zusammen einen fuer den Spieler immer
 * sichtbaren "Lichtblock" ergeben.
 */
public final class LightBlockMarker {

    private LightBlockMarker() {
    }

    /**
     * Spawnt die Anzeige- und Klick-Entity fuer einen platzierten Lichtblock an blockLocation.
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
            entity.getPersistentDataContainer().set(LightBlockKeys.MARKER, PersistentDataType.STRING, "display");
        });

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

    public static int getLevel(Interaction interaction) {
        Integer level = interaction.getPersistentDataContainer().get(LightBlockKeys.ENTITY_LEVEL, PersistentDataType.INTEGER);
        return level == null ? LightBlockItem.MAX_LEVEL : level;
    }

    /**
     * Entfernt die zu einer Interaction-Entity gehoerende TextDisplay-Entity sowie die Interaction selbst.
     */
    public static void removePair(Interaction interaction) {
        String pairedId = interaction.getPersistentDataContainer().get(LightBlockKeys.PAIRED_DISPLAY, PersistentDataType.STRING);
        if (pairedId != null) {
            try {
                UUID uuid = UUID.fromString(pairedId);
                Entity paired = interaction.getServer().getEntity(uuid);
                if (paired != null) {
                    paired.remove();
                }
            } catch (IllegalArgumentException ignored) {
                // ungueltige UUID, nichts zu tun
            }
        }
        interaction.remove();
    }

    /**
     * Entfernt alle Marker-Entities (Interaction + TextDisplay) an genau diesem Block,
     * unabhaengig davon ob die Paarung noch intakt ist. Wird fuer Aufraeumarbeiten genutzt.
     */
    public static void removeAllAt(Location blockLocation) {
        Location center = blockLocation.getBlock().getLocation().add(0.5, 0.5, 0.5);
        Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, 0.6, 0.6, 0.6);
        for (Entity entity : nearby) {
            if (entity.getPersistentDataContainer().has(LightBlockKeys.MARKER, PersistentDataType.STRING)) {
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
