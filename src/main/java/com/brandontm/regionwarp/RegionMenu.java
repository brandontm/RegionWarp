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
            ItemStack item = null;

            if (point.getDiscoveredBy().contains(who.getUniqueId().toString())) {
                item = new ItemStack(Material.GRASS_BLOCK);
            } else {
                item = new ItemStack(Material.GRAY_STAINED_GLASS);
            }

            ItemMeta meta = item.getItemMeta();

            String style = (point.getDiscoveredBy().contains(who.getUniqueId().toString())) ? ChatColor.GREEN.toString()
                    : ChatColor.GRAY.toString() + ChatColor.ITALIC.toString();

            String itemTitle = style + point.getTitle();

            meta.setDisplayName(itemTitle);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + point.getDescription());

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
            if (!(event.getWhoClicked() instanceof Player))
                return;

            final Player player = (Player) event.getWhoClicked();

            final Inventory inventory = RegionMenu.getInventory(player);

            if (event.getInventory().equals(inventory) && event.getCurrentItem() != null) {
                event.setCancelled(true);

                final RegionWarp plugin = RegionWarp.getInstance();

                final ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null || !meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "regionid"),
                        PersistentDataType.STRING))
                    return;

                final String regionId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "regionid"),
                        PersistentDataType.STRING);

                if (!WarpPointsConfig.getInstance().hasDiscoveredRegion(player, regionId)
                        && !player.hasPermission("regionwarp.warp.bypassdiscovery"))
                    return;

                final WarpPointsConfig warpPointsConfig = WarpPointsConfig.getInstance();
                final WarpPoint warpPoint = warpPointsConfig.getWarpPoint(regionId);

                final ItemStack itemInHand = player.getInventory().getItemInMainHand();

                // ignore item in hand if player should not be charged
                if (!plugin.shouldPlayerBeCharged(player) || plugin.playerHasChargeInHand(player)) {

                    if (plugin.shouldPlayerBeCharged(player))
                        player.getInventory()
                                .setItemInMainHand(itemInHand.subtract(plugin.getChargeItemStack().getAmount()));

                    player.teleport(warpPoint.getLocation());
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
            inventories.remove(event.getPlayer().getUniqueId());
        }
    }
}
