package com.brandontm.regionwarp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;

public class WarpPoint {
    private String title;
    private ProtectedRegion region;
    private Location location;
    private String description;

    public WarpPoint() {
    }

    public WarpPoint(ProtectedRegion region, Location location, String description) {
        this.title = WordUtils.capitalizeFully(region.getId().replaceAll("[-_]", " "));
        this.region = region;
        this.location = location;
        this.description = description;
    }

    /**
     * Get user friendly region name
     * 
     * @return user friendly region name
     */
    public String getTitle() {
        return title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}