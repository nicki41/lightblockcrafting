package net.lightblockcrafting.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Erzeugt und liest die Lichtblock-Items (Material.LIGHT mit Stufe 0-15).
 */
public final class LightBlockItem {

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 15;

    private LightBlockItem() {
    }

    public static ItemStack create(int level) {
        int clamped = clamp(level);
        ItemStack item = new ItemStack(Material.LIGHT);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Lichtblock", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(" [Stufe " + clamped + "]", NamedTextColor.GRAY)));
        meta.lore(List.of(
                Component.text("Lichtlevel: ", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(clamped, NamedTextColor.WHITE))
        ));
        meta.getPersistentDataContainer().set(LightBlockKeys.ITEM_LEVEL, PersistentDataType.INTEGER, clamped);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isLightBlockItem(ItemStack item) {
        if (item == null || item.getType() != Material.LIGHT || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(LightBlockKeys.ITEM_LEVEL, PersistentDataType.INTEGER);
    }

    public static int getLevel(ItemStack item) {
        if (!isLightBlockItem(item)) {
            return MAX_LEVEL;
        }
        Integer level = item.getItemMeta().getPersistentDataContainer().get(LightBlockKeys.ITEM_LEVEL, PersistentDataType.INTEGER);
        return level == null ? MAX_LEVEL : clamp(level);
    }

    public static int clamp(int level) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }
}
