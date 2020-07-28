package com.brandontm.regionwarp.event;

import org.bukkit.event.Listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        // TODO move RegionWarp sign creation to command and store
        if ("[regionwarp]".equals(event.getLine(0).trim().toLowerCase())
                || "[rw]".equals(event.getLine(0).trim().toLowerCase())) {

            if (!event.getPlayer().hasPermission("regionwarp.station.create")) {
                event.setCancelled(true);
                return;
            }

            event.setLine(0, ChatColor.DARK_BLUE + "[RegionWarp]");
            event.setLine(2, "Click derecho");
            event.setLine(3, "para abrir menu");
        }
    }
}
