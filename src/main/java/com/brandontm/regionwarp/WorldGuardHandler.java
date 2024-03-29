package com.brandontm.regionwarp;

import java.util.Set;

import com.brandontm.regionwarp.storage.WarpPointsConfig;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

/**
 * WorldGuardHandler
 */
public class WorldGuardHandler extends Handler {

    public static final Factory factory = new Factory();

    public static class Factory extends Handler.Factory<WorldGuardHandler> {
        @Override
        public WorldGuardHandler create(Session session) {
            return new WorldGuardHandler(session);
        }
    }

    protected WorldGuardHandler(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer lPlayer, Location from, Location to, ApplicableRegionSet toSet,
            Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {

        for (ProtectedRegion region : entered) {
            WarpPointsConfig warpPointsConfig = WarpPointsConfig.getInstance();
            if (warpPointsConfig.getWarpPointsConfig().isSet(region.getId())) {
                Player player = RegionWarp.getInstance().getServer().getPlayer(lPlayer.getUniqueId());

                String regionNameFormatted = WordUtils.capitalizeFully(region.getId().replaceAll("[-_]", " "));

                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append(ChatColor.AQUA);
                strBuilder.append(ChatColor.BOLD);
                strBuilder.append(regionNameFormatted);
                String subtitle = strBuilder.toString();
                String description = warpPointsConfig.getWarpPoint(region.getId()).getDescription();

                player.sendTitle("", subtitle, 10, 50, 10);
                player.sendActionBar(ChatColor.GOLD + description);

                if (!warpPointsConfig.hasDiscoveredRegion(player, region.getId())) {
                    warpPointsConfig.addRegionDiscovery(player, region.getId());

                    player.sendMessage(ChatColor.AQUA + "Has descubierto la región " + ChatColor.BOLD.toString()
                            + ChatColor.GOLD.toString() + regionNameFormatted + ".");
                    player.sendMessage(ChatColor.AQUA
                            + "Ahora puedes viajar a esta región desde cualquier punto de viaje rápido.");
                }
            }
        }
        return true;
    }
}