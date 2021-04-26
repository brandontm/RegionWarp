package com.brandontm.regionwarp;

import java.util.HashSet;
import java.util.Set;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;

public class WarpPoint {
    private ProtectedRegion region;
    private Location location;
    private String description;
    private Set<String> discoveredBy;

    public WarpPoint() {
    }

    public WarpPoint(ProtectedRegion region, Location location, String description) {
        this.region = region;
        this.location = location;
        this.description = description;
        this.discoveredBy = new HashSet<>();
    }

    public WarpPoint(ProtectedRegion region, Location location, String description, Set<String> discoveredBy) {
        this.region = region;
        this.location = location;
        this.description = description;
        this.discoveredBy = discoveredBy;
    }

    /**
     * Get user friendly region name
     * 
     * @return user friendly region name
     */
    public String getTitle() {
        return WordUtils.capitalizeFully(region.getId().replaceAll("[-_]", " "));
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

    public Set<String> getDiscoveredBy() {
        return discoveredBy;
    }

    public void setDiscoveredBy(Set<String> discoveredBy) {
        this.discoveredBy = discoveredBy;
    }
}