package com.brandontm.regionwarp;

import java.io.File;

import com.brandontm.regionwarp.command.RegionWarpCommand;
import com.brandontm.regionwarp.storage.WarpPointsConfig;
import com.sk89q.worldguard.WorldGuard;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        // TODO move teleporting to actual teleporting method
        getCommand("test").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args.length != 1) {
                    return false;
                }

                String regionId = args[0];

                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                String itemName = config.getString("teleportcharge.item");
                int quantity = config.getInt("teleportcharge.quantity");

                // TODO check if its actually configured or its set to free
                if (itemName == null || quantity == 0)
                    return true; // Teleport charge might not be configured

                WarpPointsConfig warpPointsConfig = WarpPointsConfig.getInstance();
                WarpPoint warpPoint = warpPointsConfig.getWarpPoint(regionId);

                Player player = ((Player) sender);

                Material material = Material.matchMaterial(itemName.toUpperCase());

                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand.getType().equals(material) && itemInHand.getAmount() >= quantity) {
                    itemInHand.setAmount(itemInHand.getAmount() - quantity);
                    player.getInventory().setItemInMainHand(itemInHand);
                    player.teleport(warpPoint.getLocation());
                }

                return true;
            }

        });
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
