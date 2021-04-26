package com.brandontm.regionwarp.event;

import com.brandontm.regionwarp.util.SignUtil;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();

        // ignore checks if user has permission to remove travel stations
        if (player.hasPermission("regionwarp.station.remove"))
            return;

        // check if broken block is a travel station sign
        if (SignUtil.isRegionWarpSign(block)) {
            event.setCancelled(true);
        } else {
            // check if breaking block would destroy travel station sign
            if (SignUtil.isRegionWarpSign(block.getRelative(BlockFace.UP))
                    && Tag.STANDING_SIGNS.isTagged(block.getRelative(BlockFace.UP).getType())) {

                event.setCancelled(true);
            } else {
                final BlockFace[] directions = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
                        BlockFace.WEST };

                for (BlockFace direction : directions) {
                    final Block adjBlock = block.getRelative(direction);
                    if (Tag.WALL_SIGNS.isTagged(adjBlock.getType()) && SignUtil.isRegionWarpSign(adjBlock)) {

                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
