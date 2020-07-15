package com.brandontm.regionwarp.command;

import java.util.Optional;

import com.brandontm.regionwarp.WarpPoint;
import com.brandontm.regionwarp.storage.WarpPointsConfig;
import com.brandontm.regionwarp.storage.WarpPointsConfig.RemoveStatus;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionWarpCommand implements CommandExecutor {
    private static final String MSG_NO_PERMISSION = "No tienes permiso para ejecutar este comando";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        final String operation = args[0];
        final String regionName = args[1];

        WarpPointsConfig warpPointsConfig = WarpPointsConfig.getInstance();

        if ("set".equals(operation)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Solo jugadores pueden crear region warps");
                return true;
            }

            if (!(args.length > 2)) {
                return false;
            }

            // Every argument after the second is part of the description
            String description = "";
            for (int i = 2; i < args.length; i++) {
                description += " " + args[i];
            }
            description = description.trim();

            final Player player = (Player) sender;
            final Location location = player.getLocation();

            final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final RegionQuery query = container.createQuery();
            final ApplicableRegionSet regionSet = query.getApplicableRegions(BukkitAdapter.adapt(location));

            final Optional<ProtectedRegion> region = regionSet.getRegions().stream()
                    .filter((r) -> r.getId().equals(regionName)).findAny();

            if (region.isPresent()) {
                WarpPoint warpPoint = new WarpPoint(region.get(), player.getLocation(), description);

                WarpPointsConfig.AddStatus addStatus = warpPointsConfig.addWarpPoint(warpPoint);

                String message = "";
                switch (addStatus) {
                    case CREATED:
                        message = String.format("%sWarp point para la región \"%s\" creado exitosamente",
                                ChatColor.GREEN, regionName);
                        break;
                    case REPLACED:
                        message = String.format("%sWarp point para la región \"%s\" modificado exitosamente",
                                ChatColor.GREEN, regionName);
                        break;
                    case FAILURE:
                        message = String.format("%sHubo un error al intentar crear warp point para la región \"%s\"",
                                ChatColor.RED, regionName);
                        break;
                }
                player.sendMessage(message);
            } else {
                player.sendMessage(ChatColor.RED + "El warp point de una región debe estar dentro de esta."
                        + " Verificar que usuario se encuentre dentro de la región especificada");
            }

            return true;
        } else if ("remove".equals(operation)) {
            if (args.length != 2) {
                return false;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("regionwarp.warp.remove")) {
                    player.sendMessage(MSG_NO_PERMISSION);

                    return true;
                }
            }
            RemoveStatus removeStatus = warpPointsConfig.removeWarpPoint(regionName);

            String message = "";
            switch (removeStatus) {
                case REMOVED:
                    message = String.format("%sWarp point para la región \"%s\" eliminado", ChatColor.GREEN,
                            regionName);
                    break;
                case NOT_EXISTS:
                    message = String.format("%sNo existe un warp point para la región \"%s\"", ChatColor.RED,
                            regionName);
                    break;
                case FAILURE:
                    message = String.format("%sHubo un error al intentar eliminar warp point para la región \"%s\"",
                            ChatColor.GREEN, regionName);
                    break;
            }
            sender.sendMessage(message);

            return true;
        }

        return false;
    }
}