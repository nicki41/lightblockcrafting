package net.lightblockcrafting;

import net.lightblockcrafting.command.LightBlockCommand;
import net.lightblockcrafting.listener.ChunkGuardListener;
import net.lightblockcrafting.listener.PlaceBreakListener;
import net.lightblockcrafting.listener.RecipeBookListener;
import net.lightblockcrafting.util.LightBlockItem;
import net.lightblockcrafting.util.LightBlockKeys;
import net.lightblockcrafting.util.LightBlockRecipes;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LightBlockCraftingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        LightBlockKeys.init(this);

        List<NamespacedKey> recipeKeys = LightBlockRecipes.registerAll(this);

        getServer().getPluginManager().registerEvents(new PlaceBreakListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkGuardListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeBookListener(recipeKeys), this);
        getServer().getOnlinePlayers().forEach(player -> player.discoverRecipes(recipeKeys));

        LightBlockCommand command = new LightBlockCommand();
        var pluginCommand = getCommand("lightblock");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        getLogger().info("LightBlockCrafting enabled (" + recipeKeys.size() + " recipes, levels "
                + LightBlockItem.MIN_LEVEL + "-" + LightBlockItem.MAX_LEVEL + ").");
    }
}
