package com.brandontm.regionwarp.storage;

import java.io.File;
import java.io.IOException;

import com.brandontm.regionwarp.RegionWarp;
import com.brandontm.regionwarp.WarpPoint;

import org.bukkit.Location;
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

    public WarpPoint getWarpPoint(String regionId) {
        WarpPoint warpPoint = null;
        return warpPoint;
    }

    public AddStatus addWarpPoint(WarpPoint warpPoint) {

        Location loc = warpPoint.getLocation();
        String regionId = warpPoint.getRegion().getId();

        AddStatus status = warpPointsConfig.contains(regionId) ? AddStatus.REPLACED : AddStatus.CREATED;

        warpPointsConfig.set(String.format("%s.x", regionId), loc.getX());
        warpPointsConfig.set(String.format("%s.y", regionId), loc.getY());
        warpPointsConfig.set(String.format("%s.z", regionId), loc.getZ());
        warpPointsConfig.set(String.format("%s.yaw", regionId), loc.getYaw());

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

    public RemoveStatus removeWarpPoint(String regionId) {
        if (warpPointsConfig.contains(regionId)) {
            try {
                warpPointsConfig.set(regionId, null);
                this.warpPointsConfig.save(getWarpPointsFile());

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

    public enum AddStatus {
        CREATED, REPLACED, FAILURE
    }

    public enum RemoveStatus {
        REMOVED, NOT_EXISTS, FAILURE
    }
}
