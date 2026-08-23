package net.lightblockcrafting.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Unlocks the light block recipes in the recipe book for all players, so they
 * show up there instead of only being discovered after crafting one manually.
 */
public class RecipeBookListener implements Listener {

    private final List<NamespacedKey> recipeKeys;

    public RecipeBookListener(List<NamespacedKey> recipeKeys) {
        this.recipeKeys = recipeKeys;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(recipeKeys);
    }
}
