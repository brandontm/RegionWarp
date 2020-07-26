package com.brandontm.regionwarp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.brandontm.regionwarp.storage.WarpPointsConfig;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
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

        createInventory(who);
    }

    public void openMenu() {
        final Inventory inventory = getInventory(who);

        if (inventory != null)
            who.openInventory(inventory);
    }

    public void createInventory(HumanEntity who) {
        final Inventory inventory = RegionWarp.getInstance().getServer().createInventory(null, 9, "Regiones");
        inventories.put(who.getUniqueId(), inventory);

        draw(who);
    }

    public void draw(HumanEntity who) {
        final Inventory inventory = getInventory(who);

        for (WarpPoint point : WarpPointsConfig.getInstance().getAllWarpPoints()) {
            ItemStack item = new ItemStack(Material.DIAMOND);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(point.getTitle());
            meta.setLore(Arrays.asList(point.getDescription()));
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
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null || !meta.getPersistentDataContainer()
                        .has(new NamespacedKey(RegionWarp.getInstance(), "regionid"), PersistentDataType.STRING))
                    return;

                String regionId = meta.getPersistentDataContainer()
                        .get(new NamespacedKey(RegionWarp.getInstance(), "regionid"), PersistentDataType.STRING);

                FileConfiguration config = RegionWarp.getInstance().getConfig();

                String itemName = config.getString("teleportcharge.item");
                int quantity = config.getInt("teleportcharge.quantity");

                WarpPointsConfig warpPointsConfig = WarpPointsConfig.getInstance();
                WarpPoint warpPoint = warpPointsConfig.getWarpPoint(regionId);

                Material material = Material.matchMaterial(itemName.toUpperCase());

                ItemStack itemInHand = who.getInventory().getItemInMainHand();

                // if item name
                if ((itemName == null)
                        || (itemInHand.getType().equals(material) && itemInHand.getAmount() >= quantity)) {

                    who.getInventory().setItemInMainHand(itemInHand.subtract(quantity));
                    who.teleport(warpPoint.getLocation());
                }
                event.setCancelled(true);
            } else {
                // FIXME sometimes inventory is not detected in listener for some reason and
                // items can be taken out from regionmenu inventory
                who.sendMessage(inventory.toString());
                who.sendMessage(getInventory(who).toString());
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
