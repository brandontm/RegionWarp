package com.brandontm.regionwarp.util;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

public class SignUtil {
    public static boolean isRegionWarpSign(Sign sign) {
        return ((ChatColor.DARK_BLUE + "[RegionWarp]").equals(sign.getLine(0)));
    }
}