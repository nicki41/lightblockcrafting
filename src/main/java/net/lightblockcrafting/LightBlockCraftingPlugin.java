package net.lightblockcrafting;

import net.lightblockcrafting.command.LightBlockCommand;
import net.lightblockcrafting.listener.ChunkGuardListener;
import net.lightblockcrafting.listener.LightVisibilityListener;
import net.lightblockcrafting.listener.PlaceBreakListener;
import net.lightblockcrafting.listener.RecipeBookListener;
import net.lightblockcrafting.telemetry.TelemetryReporter;
import net.lightblockcrafting.util.LightBlockItem;
import net.lightblockcrafting.util.LightBlockKeys;
import net.lightblockcrafting.util.LightBlockMarker;
import net.lightblockcrafting.util.LightBlockRecipes;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LightBlockCraftingPlugin extends JavaPlugin {

    private static final long VISIBILITY_SWEEP_INTERVAL_TICKS = 10L;

    private TelemetryReporter telemetry;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LightBlockKeys.init(this);
        LightBlockMarker.init(this);

        List<NamespacedKey> recipeKeys = LightBlockRecipes.registerAll(this);

        LightVisibilityListener visibilityListener = new LightVisibilityListener(this);

        getServer().getPluginManager().registerEvents(new PlaceBreakListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkGuardListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeBookListener(recipeKeys), this);
        getServer().getPluginManager().registerEvents(visibilityListener, this);
        getServer().getScheduler().runTaskTimer(this, visibilityListener::tick,
                VISIBILITY_SWEEP_INTERVAL_TICKS, VISIBILITY_SWEEP_INTERVAL_TICKS);
        getServer().getOnlinePlayers().forEach(player -> player.discoverRecipes(recipeKeys));

        LightBlockCommand command = new LightBlockCommand();
        var pluginCommand = getCommand("lightblock");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        telemetry = new TelemetryReporter(this);
        telemetry.start();

        getLogger().info("LightBlockCrafting enabled (" + recipeKeys.size() + " recipes, levels "
                + LightBlockItem.MIN_LEVEL + "-" + LightBlockItem.MAX_LEVEL + ").");
    }

    @Override
    public void onDisable() {
        if (telemetry != null) {
            telemetry.stop();
        }
    }
}
