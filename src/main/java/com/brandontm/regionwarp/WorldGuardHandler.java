package com.brandontm.regionwarp;

import java.util.Set;

import com.brandontm.regionwarp.storage.DiscoveredRegionsConfig;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

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

        DiscoveredRegionsConfig discoveredRegionsConfig = DiscoveredRegionsConfig.getInstance();
        for (ProtectedRegion region : entered) {
            Player player = RegionWarp.getInstance().getServer().getPlayer(lPlayer.getUniqueId());

            String regionNameFormatted = "";
            // Capitalize region name and replace _ and - with spaces
            if (region.getId().length() > 0) {
                if (region.getId().length() == 1)
                    regionNameFormatted = region.getId().toUpperCase();
                else
                    regionNameFormatted = region.getId().substring(0, 1).toUpperCase() + region.getId().substring(1);

                regionNameFormatted = regionNameFormatted.replaceAll("[-_]", " ");
            }

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(ChatColor.AQUA);
            strBuilder.append(ChatColor.BOLD);
            strBuilder.append(regionNameFormatted);
            String subtitle = strBuilder.toString();

            player.sendTitle("", subtitle, 10, 50, 10);
            player.sendActionBar(
                    ChatColor.GOLD + "Ahora puedes viajar a esta región desde cualquier punto de viaje rápido");

            if (!discoveredRegionsConfig.hasDiscoveredRegion(player, region.getId())) {
                discoveredRegionsConfig.addDiscoveredRegion(player, region.getId());
            }
        }
        return true;
    }
}