package com.brandontm.regionwarp.command;

// import com.brandontm.regionwarp.RegionWarp;

import org.bukkit.ChatColor;
// import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for "setstation" command
 */
public class SetStationCommand implements CommandExecutor {
    // private static final NamespacedKey KEY_IS_STATION = new
    // NamespacedKey(RegionWarp.getInstance(), "isstation");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo los jugadores pueden usar este comando");
            return true;
        }
        if (args.length > 0)
            return false;

        final Player player = (Player) sender;
        final Block block = player.getTargetBlock(4);
        if (block == null || !(block.getState() instanceof Sign)) {
            player.sendMessage("Debes usar este comando sobre un letrero para convertirlo en estación de viaje rápido");
            return true;
        }

        // final Sign sign = (Sign) block.getState();

        return true;
    }
}