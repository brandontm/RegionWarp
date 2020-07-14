package com.brandontm.regionwarp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;

public class WarpPoint {
    private ProtectedRegion region;
    private Location location;

    public WarpPoint() {
    }

    public WarpPoint(ProtectedRegion region, Location location) {
        this.region = region;
        this.location = location;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public void setRegion(ProtectedRegion region) {
        this.region = region;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}