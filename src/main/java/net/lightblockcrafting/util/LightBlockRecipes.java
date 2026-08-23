package net.lightblockcrafting.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers one recipe per craftable light level (1-15, level 0 is deliberately
 * not craftable). Every recipe consists of 1x glass plus glowstone:
 * <ul>
 *     <li>Level 1-8: 1x glass + N x glowstone dust (N = level)</li>
 *     <li>Level 9-15: 1x glass + N x glowstone block (N = level - 7, i.e. 2-8 blocks)</li>
 * </ul>
 * This means every recipe fits in a regular 3x3 crafting grid (1 glass + up to 8
 * more ingredients) and every level has exactly one fixed recipe - no way to
 * adjust it afterwards.
 */
public final class LightBlockRecipes {

    private LightBlockRecipes() {
    }

    public static List<NamespacedKey> registerAll(Plugin plugin) {
        List<NamespacedKey> keys = new ArrayList<>();

        for (int level = 1; level <= 8; level++) {
            keys.add(registerRecipe(plugin, level, Material.GLOWSTONE_DUST, level));
        }

        for (int level = 9; level <= LightBlockItem.MAX_LEVEL; level++) {
            keys.add(registerRecipe(plugin, level, Material.GLOWSTONE, level - 7));
        }

        return keys;
    }

    private static NamespacedKey registerRecipe(Plugin plugin, int level, Material glowstoneIngredient, int amount) {
        NamespacedKey key = new NamespacedKey(plugin, "lightblock_level_" + level);
        ShapelessRecipe recipe = new ShapelessRecipe(key, LightBlockItem.create(level));
        recipe.addIngredient(1, Material.GLASS);
        recipe.addIngredient(amount, glowstoneIngredient);
        plugin.getServer().addRecipe(recipe);
        return key;
    }
}
