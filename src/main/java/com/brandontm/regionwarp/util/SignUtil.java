package com.brandontm.regionwarp.util;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignUtil {
    public static boolean isRegionWarpSign(Block block) {
        if (!(block.getState() instanceof Sign))
            return false;

        Sign sign = (Sign) block.getState();
        return ((ChatColor.DARK_BLUE + "[RegionWarp]").equals(sign.getLine(0)));
    }
}