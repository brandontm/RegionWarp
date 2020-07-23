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

        // check if broken block is a regionwarp sign
        if (SignUtil.isRegionWarpSign(block) && !player.isOp())
            event.setCancelled(true);

        // check if breaking block would destroy regionwarp sign
        if (SignUtil.isRegionWarpSign(block.getRelative(BlockFace.UP))
                && Tag.STANDING_SIGNS.isTagged(block.getRelative(BlockFace.UP).getType()) && !player.isOp()) {

            event.setCancelled(true);
        } else {
            final BlockFace[] directions = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
                    BlockFace.WEST };

            for (BlockFace direction : directions) {
                final Block adjBlock = block.getRelative(direction);
                if (Tag.WALL_SIGNS.isTagged(adjBlock.getType()) && SignUtil.isRegionWarpSign(adjBlock)
                        && !player.isOp()) {

                    event.setCancelled(true);
                }
            }
        }
        //
    }
}
