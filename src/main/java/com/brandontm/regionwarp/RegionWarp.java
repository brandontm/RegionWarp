package com.brandontm.regionwarp;

import java.io.File;
import java.io.IOException;

import com.brandontm.regionwarp.command.RegionWarpCommand;
import com.brandontm.regionwarp.event.BlockListener;
import com.brandontm.regionwarp.event.PlayerInteractListener;
import com.brandontm.regionwarp.event.SignChangeListener;
import com.brandontm.regionwarp.util.SkullUtil;
import com.sk89q.worldguard.WorldGuard;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
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
        if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WorldGuardHandler.factory,
                null)) {

            getLogger().severe("No se pudo registrar RegionWarp WorldGuardHandler");
            getLogger().severe("Se desactivara el plugin");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            SkullUtil.loadAlphabetSkulls();
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().severe("Hubo un error al intentar cargar las cabezas del menu de regiones. ");
            getLogger().severe("No se mostrarán iniciales en el menu. Reiniciar podría solucionar este error.");
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

    public boolean playerHasChargeInHand(Player player) {
        final FileConfiguration config = RegionWarp.getInstance().getConfig();

        final String itemName = config.getString("teleportcharge.item", Material.DIRT.toString());
        int quantity = config.getInt("teleportcharge.quantity", 1);
        if (quantity < 0)
            quantity = 0;

        final Material material = Material.matchMaterial(itemName.toUpperCase());

        final ItemStack itemInHand = player.getInventory().getItemInMainHand();
        return (itemInHand.getType().equals(material) && itemInHand.getAmount() >= quantity);
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
