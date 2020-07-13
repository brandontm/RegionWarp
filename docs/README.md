A plugin that allows users to teleport between different regions they have already discovered.

# Permissions 
- createRegionWarp
- warpWithoutDiscovering
- warpWithoutCharge

# Requirements
1. Player must be able to create a warp point within a region:
    - Player must have the 'createRegionWarp' permission.
    - Point must be inside a specificated WorldGuard region.

2. Player must be notified when discovering a new region he can teleport to.

3. Player must be able to teleport to a warp point from a designated area:
    - Player will be charged a set amount of a certain item. This will be configurable.
    - 'warpWithoutCharge' permission allows player to teleport without being charged.
    - 'warpWithoutDiscovering' permission allows player to teleport to regions he hasn't discovered yet.