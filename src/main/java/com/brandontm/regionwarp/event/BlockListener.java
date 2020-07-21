package com.brandontm.regionwarp.event;

import com.brandontm.regionwarp.util.SignUtil;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        boolean isSign = event.getBlock().getState() instanceof Sign;

        if (isSign && !player.isOp()) { // TODO check permissions
            Sign sign = (Sign) event.getBlock().getState();

            if (SignUtil.isRegionWarpSign(sign))
                event.setCancelled(true);
        }
    }
}
