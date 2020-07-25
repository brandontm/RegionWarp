package com.brandontm.regionwarp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * RegionMenu
 */
public class RegionMenu {
    public static final Map<UUID, Inventory> inventories = new HashMap<>();

    public static void openMenu(HumanEntity who) {
        createInventory(who);

        final Inventory inventory = getInventory(who);
        if (inventory != null)
            who.openInventory(inventory);
    }

    public static void createInventory(HumanEntity who) {
        inventories.put(who.getUniqueId(), RegionWarp.getInstance().getServer().createInventory(null, 9, "Regiones"));

        draw(who);
    }

    public static void draw(HumanEntity who) {
        final Inventory inventory = getInventory(who);

        inventory.addItem(new ItemStack(Material.DIAMOND));
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
