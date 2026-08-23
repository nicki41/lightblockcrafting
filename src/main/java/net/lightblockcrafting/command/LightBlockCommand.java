package net.lightblockcrafting.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.lightblockcrafting.util.LightBlockItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * /lightblock give <level> [player] - gives a light block of the requested level.
 */
public class LightBlockCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("Usage: /lightblock give <level 0-15> [player]", NamedTextColor.RED));
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Level must be a number between 0 and 15.", NamedTextColor.RED));
            return true;
        }
        if (level < LightBlockItem.MIN_LEVEL || level > LightBlockItem.MAX_LEVEL) {
            sender.sendMessage(Component.text("Level must be between 0 and 15.", NamedTextColor.RED));
            return true;
        }

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found: " + args[2], NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(Component.text("Please specify a player.", NamedTextColor.RED));
            return true;
        }

        ItemStack item = LightBlockItem.create(level);
        target.getInventory().addItem(item);
        sender.sendMessage(Component.text("Gave a level " + level + " light block to " + target.getName() + ".", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.add("give");
        } else if (args.length == 2) {
            for (int i = LightBlockItem.MIN_LEVEL; i <= LightBlockItem.MAX_LEVEL; i++) {
                result.add(String.valueOf(i));
            }
        } else if (args.length == 3) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                result.add(player.getName());
            }
        }
        return result;
    }
}
