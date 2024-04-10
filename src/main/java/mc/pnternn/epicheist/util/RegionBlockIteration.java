package mc.pnternn.epicheist.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class RegionBlockIteration {
    public List<Location> getRegionBlocks(String worldName, String regionName) {
        World world = Bukkit.getWorld(worldName);
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion(regionName);

        Location min = BukkitAdapter.adapt(world, region.getMinimumPoint());
        Location max = BukkitAdapter.adapt(world, region.getMaximumPoint());

        List<Location> locations = new ArrayList<>();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }
    public static int getVaultRegionMinimumY() {
        World world = Bukkit.getWorld(ConfigurationHandler.getValue("regions.world-name"));
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion(ConfigurationHandler.getValue("regions.vault-name"));
        assert region != null;
        return region.getMinimumPoint().getBlockY();
    }
}
