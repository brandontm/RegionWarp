package com.brandontm.regionwarp.event;

import com.brandontm.regionwarp.RegionMenu;
import com.brandontm.regionwarp.RegionWarp;
import com.brandontm.regionwarp.util.SignUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && SignUtil.isRegionWarpSign(event.getClickedBlock())) {
            final Player player = event.getPlayer();

            if (!player.hasPermission("regionwarp.warp.use")) {
                player.sendMessage(ChatColor.DARK_RED + "No tienes permiso para usar el viaje rápido");
                return;
            }

            if (!RegionWarp.getInstance().playerHasChargeInHand(player)
                    && RegionWarp.getInstance().shouldPlayerBeCharged(player)) {

                player.sendMessage(ChatColor.RED + "No tienes la cuota en mano");
                return;
            }

            RegionMenu menu = new RegionMenu(player);
            menu.openMenu();
        }
    }
}