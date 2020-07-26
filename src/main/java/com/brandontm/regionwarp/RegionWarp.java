package com.brandontm.regionwarp;

import java.io.File;

import com.brandontm.regionwarp.command.RegionWarpCommand;
import com.brandontm.regionwarp.event.BlockListener;
import com.brandontm.regionwarp.event.PlayerInteractListener;
import com.brandontm.regionwarp.event.SignChangeListener;
import com.brandontm.regionwarp.storage.WarpPointsConfig;
import com.sk89q.worldguard.WorldGuard;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RegionWarp extends JavaPlugin {
    private final File configFile = new File(getDataFolder(), "config.yml");

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
        saveDefaultConfig();
        reloadConfig();

        registerCommands();
        registerEvents();
    }

    private void registerEvents() {
        PluginManager pManager = getServer().getPluginManager();

        pManager.registerEvents(new SignChangeListener(), this);
        pManager.registerEvents(new BlockListener(), this);
        pManager.registerEvents(new PlayerInteractListener(), this);
        pManager.registerEvents(new RegionMenu.InventoryListener(), this);
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
