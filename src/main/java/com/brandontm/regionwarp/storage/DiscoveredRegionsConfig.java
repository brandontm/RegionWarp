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

    public void addDiscoveredRegion(Player player, String regionId) {
        Set<String> discoveredRegions = getDiscoveredRegions(player);
        discoveredRegions.add(regionId);

        try {
            List<String> list = discoveredRegions.stream().collect(Collectors.toList());
            getDiscoveredRegionsConfig().set(player.getUniqueId().toString(), list);
            getDiscoveredRegionsConfig().save(getDiscoveredRegionsFile());
        } catch (IOException ex) {
            RegionWarp.getInstance().getLogger()
                    .severe("Hubo un error al intentar guardar una región descubierta."
                            + " Puede ser un error interno del plugin o del servidor."
                            + " Contactar a Brandon si sucede con frecuencia");
        }
    }

    public Set<String> getDiscoveredRegions(Player player) {
        Set<String> discoveredRegions = getDiscoveredRegionsConfig().isSet(player.getUniqueId().toString())
                ? getDiscoveredRegionsConfig().getStringList(player.getUniqueId().toString()).stream()
                        .collect(Collectors.toSet())
                : new HashSet<>();

        return discoveredRegions;
    }

    public boolean hasDiscoveredRegion(Player player, String regionId) {
        return getDiscoveredRegions(player).contains(regionId);
    }

    public static DiscoveredRegionsConfig getInstance() {
        if (instance == null) {
            File file = RegionWarp.getInstance().getDiscoveredRegionsFile();
            instance = new DiscoveredRegionsConfig(file);
        }
        return instance;
    }
}
