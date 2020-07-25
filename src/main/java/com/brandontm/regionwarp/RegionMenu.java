package com.brandontm.regionwarp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.brandontm.regionwarp.storage.WarpPointsConfig;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * RegionMenu
 */
public class RegionMenu {
    public static final Map<UUID, Inventory> inventories = new HashMap<>();

    public static void openMenu(HumanEntity who) {
        createInventory(who);
    }

    public static void createInventory(HumanEntity who) {
        final Inventory inventory = RegionWarp.getInstance().getServer().createInventory(null, 9, "Regiones");
        inventories.put(who.getUniqueId(), inventory);

        draw(who);
    }

    public static void draw(HumanEntity who) {
        final Inventory inventory = getInventory(who);

        for (WarpPoint point : WarpPointsConfig.getInstance().getAllWarpPoints()) {
            ItemStack item = new ItemStack(Material.DIAMOND);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(point.getTitle());
            meta.setLore(Arrays.asList(point.getDescription()));

            item.setItemMeta(meta);

            inventory.addItem(item);
        }

        if (inventory != null)
            who.openInventory(inventory);

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
            final Inventory inventory = RegionMenu.getInventory(event.getWhoClicked());

            if (event.getInventory().equals(inventory)) {
                event.setCancelled(true);
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
            inventories.remove(event.getPlayer().getUniqueId());
        }
    }
}
