package com.brandontm.regionwarp;

import org.bukkit.plugin.java.JavaPlugin;

public class RegionWarp extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello spigotMC!");
    }

    @Override
    public void onDisable() {
        getLogger().info("bye spigotMC!");
    }
}
