package com.brandontm.regionwarp;

import java.io.File;

import com.brandontm.regionwarp.commands.RegionWarpCommand;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionWarp extends JavaPlugin {
    private static RegionWarp instance;

    public RegionWarp() {
        instance = this;
    }

    public static RegionWarp getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getLogger().info("RegionWarp started");

        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("RegionWarp stopped");
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
}
