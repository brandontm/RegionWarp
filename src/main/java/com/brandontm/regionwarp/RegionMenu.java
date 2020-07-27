package com.brandontm.regionwarp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.brandontm.regionwarp.storage.WarpPointsConfig;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * RegionMenu
 */
public class RegionMenu {
    public static final Map<UUID, Inventory> inventories = new HashMap<>();
    private HumanEntity who;

    public RegionMenu(HumanEntity who) {
        this.who = who;

        createInventory();
    }

    public void openMenu() {
        final Inventory inventory = getInventory(who);

        if (inventory != null)
            who.openInventory(inventory);
    }

    public void createInventory() {
        final Inventory inventory = RegionWarp.getInstance().getServer().createInventory(null, 9, "Regiones");
        inventories.put(who.getUniqueId(), inventory);

        draw();
    }

    public void draw() {
        final Inventory inventory = getInventory(who);

        for (WarpPoint point : WarpPointsConfig.getInstance().getAllWarpPoints()) {
            // TODO show region initial item with player head
            ItemStack item = new ItemStack(Material.DIAMOND);
            ItemMeta meta = item.getItemMeta();

            String style = (point.getDiscoveredBy().contains(who.getUniqueId().toString())) ? ChatColor.GREEN.toString()
                    : ChatColor.GRAY.toString() + ChatColor.ITALIC.toString();

            String itemTitle = style + point.getTitle();

            meta.setDisplayName(itemTitle);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + point.getDescription());

            if (point.getDiscoveredBy().contains(who.getUniqueId().toString()))
                lore.add(ChatColor.GRAY + "No descubierto");

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey(RegionWarp.getInstance(), "regionid"),
                    PersistentDataType.STRING, point.getRegion().getId());

            item.setItemMeta(meta);

            inventory.addItem(item);
        }
    }

    public static Inventory getInventory(HumanEntity who) {
        return inventories.get(who.getUniqueId());
    }

    /**
     * Listener to handle RegionMenu events
     */
    public static class InventoryListener implements Listener {
        @EventHandler
        public void onInventoryClick(final InventoryClickEvent event) {
            HumanEntity who = event.getWhoClicked();
            final Inventory inventory = RegionMenu.getInventory(who);

            if (event.getInventory().equals(inventory) && event.getCurrentItem() != null) {
                event.setCancelled(true);

                if (!(who instanceof Player))
                    return;

                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null || !meta.getPersistentDataContainer()
                        .has(new NamespacedKey(RegionWarp.getInstance(), "regionid"), PersistentDataType.STRING))
                    return;

                String regionId = meta.getPersistentDataContainer()
                        .get(new NamespacedKey(RegionWarp.getInstance(), "regionid"), PersistentDataType.STRING);

                if (!WarpPointsConfig.getInstance().hasDiscoveredRegion((Player) who, regionId))
                    return;

                FileConfiguration config = RegionWarp.getInstance().getConfig();

                String itemName = config.getString("teleportcharge.item", Material.DIRT.toString());
                int quantity = config.getInt("teleportcharge.quantity", 1);
                if (quantity < 0)
                    quantity = 0;

                WarpPointsConfig warpPointsConfig = WarpPointsConfig.getInstance();
                WarpPoint warpPoint = warpPointsConfig.getWarpPoint(regionId);

                Material material = Material.matchMaterial(itemName.toUpperCase());

                ItemStack itemInHand = who.getInventory().getItemInMainHand();
                if (itemInHand.getType().equals(material) && itemInHand.getAmount() >= quantity) {
                    // charge only when config is set and quantity is bigger than 0
                    who.getInventory().setItemInMainHand(itemInHand.subtract(quantity));

                    who.teleport(warpPoint.getLocation());
                }
            }
        }

        @EventHandler
        public void onInventoryDrag(final InventoryDragEvent event) {
            final Inventory inventory = RegionMenu.getInventory(event.getWhoClicked());

            if (event.getInventory().equals(inventory)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClose(final InventoryCloseEvent event) {
            getInventory(event.getPlayer()).clear(); // clear region menu inventory just in case

            inventories.remove(event.getPlayer().getUniqueId());
        }
    }
}
