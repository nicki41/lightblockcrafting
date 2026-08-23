package net.lightblockcrafting.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Schaltet allen Spielern die Lichtblock-Rezepte im Rezeptbuch frei, damit sie
 * dort auftauchen statt erst nach dem ersten manuellen Craften entdeckt zu werden.
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
