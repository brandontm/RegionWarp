package com.brandontm.regionwarp;

import java.io.File;

import com.brandontm.regionwarp.command.RegionWarpCommand;
import com.sk89q.worldguard.WorldGuard;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionWarp extends JavaPlugin {
    public final File configFile = new File(getDataFolder(), "config.yml");

    private static RegionWarp instance;

    public RegionWarp() {
        instance = this;
    }

    public static RegionWarp getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WorldGuardHandler.factory,
                null)) {

            getLogger().severe("[RegionWarp] No se pudo registrar RegionWarp WorldGuardHandler");
            getLogger().severe("[RegionWarp] Se desactivara el plugin");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Init config.yml file
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }

        registerCommands();
    }

    private void registerCommands() {
        CommandExecutor regionWarpCommand = new RegionWarpCommand();

        getCommand("regionwarp").setExecutor(regionWarpCommand);
        getCommand("rw").setExecutor(regionWarpCommand);
    }

    public File getWarpPointsFile() {
        final File warpPointsFile = new File(getDataFolder(), "warppoints.yml");
        if (!warpPointsFile.exists()) {
            saveResource("warppoints.yml", false);
        }

        return warpPointsFile;
    }

    public File getDiscoveredRegionsFile() {
        final File discoveredRegionsFile = new File(getDataFolder(), "discoveredregions.yml");
        if (!discoveredRegionsFile.exists()) {
            saveResource("discoveredregions.yml", false);
        }

        return discoveredRegionsFile;
    }
}
