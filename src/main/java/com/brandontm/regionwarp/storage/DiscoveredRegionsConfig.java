package com.brandontm.regionwarp.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.brandontm.regionwarp.RegionWarp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * DiscoveredRegionsConfig
 */
public class DiscoveredRegionsConfig {
    private static DiscoveredRegionsConfig instance;
    private File discoveredRegionsFile;
    private FileConfiguration discoveredRegionsConfig;

    private DiscoveredRegionsConfig() {
    }

    private DiscoveredRegionsConfig(File discoveredRegionsFile) {
        this.discoveredRegionsFile = discoveredRegionsFile;
        this.discoveredRegionsConfig = YamlConfiguration.loadConfiguration(discoveredRegionsFile);
    }

    public File getDiscoveredRegionsFile() {
        return discoveredRegionsFile;
    }

    public FileConfiguration getDiscoveredRegionsConfig() {
        return discoveredRegionsConfig;
    }

    /**
     * Gets a set of UUIDs of players that have discovered the specified region
     * 
     * @param regionId Region id
     * @return List of player UUIDs
     */
    public Set<String> getRegionDiscoveries(String regionId) {
        Set<String> discoveredRegions = getDiscoveredRegionsConfig().isSet(regionId)
                ? getDiscoveredRegionsConfig().getStringList(regionId).stream().collect(Collectors.toSet())
                : new HashSet<>();

        return discoveredRegions;
    }

    /**
     * Add a region discovery by player to Discovered Regions Config
     * {@link FileConfiguration}
     * 
     * @param player   Player
     * @param regionId Region Id
     */
    public void addRegionDiscovery(Player player, String regionId) {
        Set<String> discoveredRegions = getRegionDiscoveries(regionId);
        discoveredRegions.add(player.getUniqueId().toString());

        try {
            List<String> list = discoveredRegions.stream().collect(Collectors.toList());
            getDiscoveredRegionsConfig().set(regionId, list);
            getDiscoveredRegionsConfig().save(getDiscoveredRegionsFile());
        } catch (IOException ex) {
            RegionWarp.getInstance().getLogger()
                    .severe("Hubo un error al intentar guardar una región descubierta."
                            + " Puede ser un error interno del plugin o del servidor."
                            + " Contactar a Brandon si sucede con frecuencia");
        }
    }

    public void removeRegionDiscoveries(String regionId) {
        getDiscoveredRegionsConfig().set(regionId, null);
        try {
            getDiscoveredRegionsConfig().save(getDiscoveredRegionsFile());
        } catch (IOException ex) {
            RegionWarp.getInstance().getLogger()
                    .severe("Hubo un error al intentar remover los descubrimientos de una región."
                            + " Puede ser un error interno del plugin o del servidor."
                            + " Contactar a Brandon si sucede con frecuencia");
        }
    }

    /**
     * Returns true if the player has discovered the region
     * 
     * @param player   Player
     * @param regionId Region Id
     * @return true if player has discovered the region
     */
    public boolean hasDiscoveredRegion(Player player, String regionId) {
        return getRegionDiscoveries(regionId).contains(player.getUniqueId().toString());
    }

    public static DiscoveredRegionsConfig getInstance() {
        if (instance == null) {
            File file = RegionWarp.getInstance().getDiscoveredRegionsFile();
            instance = new DiscoveredRegionsConfig(file);
        }
        return instance;
    }
}
