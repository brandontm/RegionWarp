package com.brandontm.regionwarp.event;

import com.brandontm.regionwarp.RegionMenu;
import com.brandontm.regionwarp.util.SignUtil;

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

            // TODO don't show menu if user has no permission to fast travel
            RegionMenu.openMenu(player);
        }
    }
}