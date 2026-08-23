package net.lightblockcrafting.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Registriert je ein Rezept pro craftbarer Lichtstufe (1-15, Stufe 0 ist bewusst
 * nicht craftbar). Jedes Rezept besteht aus 1x Glas plus Glowstone:
 * <ul>
 *     <li>Stufe 1-8: 1x Glas + N x Glowstone-Staub (N = Stufe)</li>
 *     <li>Stufe 9-15: 1x Glas + N x Glowstone-Block (N = Stufe - 7, also 2-8 Bloecke)</li>
 * </ul>
 * Damit passt jedes Rezept in ein normales 3x3-Crafting-Feld (1 Glas + max. 8
 * weitere Zutaten) und jede Stufe hat genau ein festes Rezept - keine
 * nachtraegliche Anpassung moeglich.
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
