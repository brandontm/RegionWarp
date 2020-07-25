package com.brandontm.regionwarp.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.brandontm.regionwarp.RegionWarp;
import com.brandontm.regionwarp.WarpPoint;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * WarpPointsConfig
 */
public class WarpPointsConfig {
    private static WarpPointsConfig instance;
    private File warpPointsFile;
    private FileConfiguration warpPointsConfig;

    private WarpPointsConfig() {
    }

    private WarpPointsConfig(File warpPointsFile) {
        this.warpPointsFile = warpPointsFile;
        this.warpPointsConfig = YamlConfiguration.loadConfiguration(warpPointsFile);
    }

    public File getWarpPointsFile() {
        return warpPointsFile;
    }

    public FileConfiguration getWarpPointsConfig() {
        return warpPointsConfig;
    }

    /**
     * Get all warp points in config
     * 
     * @return A list of all warp point registered in warppoints config
     */
    public List<WarpPoint> getAllWarpPoints() {
        List<WarpPoint> warpPoints = new ArrayList<>();

        for (final String regionId : getWarpPointsConfig().getKeys(false)) {

            String w = getWarpPointsConfig().getString(String.format("%s.world", regionId));
            World world = RegionWarp.getInstance().getServer().getWorld(w);
            double x = getWarpPointsConfig().getDouble(String.format("%s.x", regionId));
            double y = getWarpPointsConfig().getDouble(String.format("%s.y", regionId));
            double z = getWarpPointsConfig().getDouble(String.format("%s.z", regionId));
            float yaw = (float) getWarpPointsConfig().getDouble(String.format("%s.yaw", regionId));

            String description = getWarpPointsConfig().getString(String.format("%s.description", regionId));

            Location location = new Location(world, x, y, z, yaw, 0f);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            ProtectedRegion region = regions.getRegion(regionId);

            warpPoints.add(new WarpPoint(region, location, description));
        }

        return warpPoints;
    }

    /**
     * Gets warp point with id {@code regionId}
     * 
     * @param regionId Region id
     * @return Warp point with id {@code regionId} or null if not found
     */
    public WarpPoint getWarpPoint(String regionId) {
        WarpPoint warpPoint = null;

        if (getWarpPointsConfig().isSet(regionId)) {
            String w = getWarpPointsConfig().getString(String.format("%s.world", regionId));
            World world = RegionWarp.getInstance().getServer().getWorld(w);
            double x = getWarpPointsConfig().getDouble(String.format("%s.x", regionId));
            double y = getWarpPointsConfig().getDouble(String.format("%s.y", regionId));
            double z = getWarpPointsConfig().getDouble(String.format("%s.z", regionId));
            float yaw = (float) getWarpPointsConfig().getDouble(String.format("%s.yaw", regionId));

            String description = getWarpPointsConfig().getString(String.format("%s.description", regionId));

            Location location = new Location(world, x, y, z, yaw, 0f);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            ProtectedRegion region = regions.getRegion(regionId);

            warpPoint = new WarpPoint(region, location, description);
        }

        return warpPoint;
    }

    /**
     * Add {@link WarpPoint} to Warp Points Config {@link FileConfiguration}
     * 
     * @param warpPoint Warp Point
     * @return {@code AddStatus.CREATED} if Warp Point was created.
     *         {@code AddStatus.REPLACED} if Warp Point was updated.
     *         {@code AddStatus.FAILURE} if there was an error trying to save Warp
     *         Point.
     */
    public AddStatus addWarpPoint(WarpPoint warpPoint) {

        Location loc = warpPoint.getLocation();
        String regionId = warpPoint.getRegion().getId();

        AddStatus status = warpPointsConfig.contains(regionId) ? AddStatus.REPLACED : AddStatus.CREATED;

        warpPointsConfig.set(String.format("%s.world", regionId), loc.getWorld().getName());
        warpPointsConfig.set(String.format("%s.x", regionId), loc.getX());
        warpPointsConfig.set(String.format("%s.y", regionId), loc.getY());
        warpPointsConfig.set(String.format("%s.z", regionId), loc.getZ());
        warpPointsConfig.set(String.format("%s.yaw", regionId), loc.getYaw());
        warpPointsConfig.set(String.format("%s.description", regionId), warpPoint.getDescription());

        try {
            this.warpPointsConfig.save(getWarpPointsFile());
        } catch (IOException ex) {
            RegionWarp.getInstance().getLogger()
                    .severe("Hubo un error al intentar crear un warp point."
                            + " Puede ser un error interno del plugin o del servidor."
                            + " Contactar a Brandon si sucede con frecuencia");

            return AddStatus.FAILURE;
        }

        return status;
    }

    /**
     * Remove warp point of region {@code regionId}
     * 
     * @param regionId Region Id
     * @return {@code RemoveStatus.REMOVED} if removed successfully.
     *         {@code RemoveStatus.NOT_EXISTS} if requested warp point does not
     *         exist. {@code RemoveStatus.FAILURE} if there was an error trying to
     *         remove warp point.
     */
    public RemoveStatus removeWarpPoint(String regionId) {
        if (warpPointsConfig.contains(regionId)) {
            try {
                warpPointsConfig.set(regionId, null);
                this.warpPointsConfig.save(getWarpPointsFile());

                DiscoveredRegionsConfig.getInstance().removeRegionDiscoveries(regionId);

                return RemoveStatus.REMOVED;
            } catch (IOException ex) {
                RegionWarp.getInstance().getLogger()
                        .severe("Hubo un error al intentar eliminar un warp point."
                                + " Puede ser un error interno del plugin o del servidor."
                                + " Contactar a Brandon si sucede con frecuencia");

                return RemoveStatus.FAILURE;
            }
        } else {
            return RemoveStatus.NOT_EXISTS;
        }
    }

    public static WarpPointsConfig getInstance() {
        if (instance == null) {
            File file = RegionWarp.getInstance().getWarpPointsFile();
            instance = new WarpPointsConfig(file);
        }
        return instance;
    }

    /**
     * 
     */
    public enum AddStatus {
        CREATED, REPLACED, FAILURE
    }

    public enum RemoveStatus {
        REMOVED, NOT_EXISTS, FAILURE
    }
}
