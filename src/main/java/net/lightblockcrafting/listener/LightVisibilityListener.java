package net.lightblockcrafting.listener;

import net.lightblockcrafting.util.LightBlockMarker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps each player's light-level numbers in sync with whether they are
 * currently holding a light block. The item-held/swap-hands events cover the
 * common cases instantly; a periodic sweep (see {@link #tick()}) catches
 * everything else (dropping, consuming, creative-inventory edits, ...) within
 * half a second.
 */
public class LightVisibilityListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Boolean> lastHolding = new HashMap<>();

    public LightVisibilityListener(Plugin plugin) {
        this.plugin = plugin;
    }

    public void tick() {
        Bukkit.getOnlinePlayers().forEach(this::updatePlayer);
    }

    private void updatePlayer(Player player) {
        boolean holding = LightBlockMarker.isHoldingLightBlock(player);
        Boolean previous = lastHolding.put(player.getUniqueId(), holding);
        if (previous == null || previous != holding) {
            LightBlockMarker.applyVisibility(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastHolding.remove(event.getPlayer().getUniqueId());
    }

    // The inventory hasn't actually changed slot yet at event time, so the
    // check is deferred to the next tick rather than read from the event.
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> updatePlayer(event.getPlayer()));
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> updatePlayer(event.getPlayer()));
    }
}
