package net.lightblockcrafting.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * Central PersistentDataContainer keys used by the plugin.
 */
public final class LightBlockKeys {

    public static NamespacedKey ITEM_LEVEL;
    public static NamespacedKey MARKER;
    public static NamespacedKey ENTITY_LEVEL;
    public static NamespacedKey PAIRED_DISPLAY;

    private LightBlockKeys() {
    }

    public static void init(Plugin plugin) {
        ITEM_LEVEL = new NamespacedKey(plugin, "lightblock_item_level");
        MARKER = new NamespacedKey(plugin, "lightblock_marker");
        ENTITY_LEVEL = new NamespacedKey(plugin, "lightblock_entity_level");
        PAIRED_DISPLAY = new NamespacedKey(plugin, "lightblock_paired_display");
    }
}
