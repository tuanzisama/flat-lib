package ink.tuanzi.flatlib.bukkit;

import io.papermc.lib.PaperLib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.concurrent.CompletableFuture;

public class LocationUtil {

    /**
     * Get the highest safe block.
     * <p>For more information, see {@link this#getHighestSafeBlockAt}.</p>
     *
     * @param location a location
     * @return Highest safe block
     */
    public static Location getHighestBlockAt(Location location) {
        return location.getWorld().getHighestBlockAt(location).getLocation();
    }

    /**
     * Get the highest safe block.
     * <p>
     * Useful for random teleport or otherwise.
     * It will try to ignore all blocks that are not friendly to players in survival mode.
     * </p>
     *
     * @param location a location
     * @return Highest safe block
     */
    public static Location getHighestSafeBlockAt(Location location) {
        if (World.Environment.NETHER.equals(location.getWorld().getEnvironment())) {
            Location cloneLoc = location.clone();
            Location topLoc = null;

            for (int i = 1; i < 127; i++) {
                cloneLoc.setY(i);
                Block block = cloneLoc.getBlock();
                Block block2 = cloneLoc.clone().add(0.0d, 1.0d, 0.0d).getBlock();
                Block block3 = cloneLoc.clone().add(0.0d, -1.0d, 0.0d).getBlock();

                if (BlockUtil.isAir(block) && BlockUtil.isAir(block2) && !BlockUtil.isUnsafeBlock(block3)) {

                    // player underfoot block
                    Block baseBlock = null;

                    Location loc1 = cloneLoc.clone();
                    for (int j = i; j >= 0; j--) {
                        loc1.setY(j);
                        Block lowestBlock = loc1.getBlock();
                        if (BlockUtil.isAir(lowestBlock)) {
                            continue;
                        }

                        if (!BlockUtil.isUnsafeBlock(lowestBlock)) {
                            baseBlock = lowestBlock;
                        }
                        break;
                    }

                    if (baseBlock != null) {
                        topLoc = cloneLoc.subtract(0, 1d, 0);
                        break;
                    }
                }
            }
            return topLoc;
        } else if (World.Environment.NORMAL.equals(location.getWorld().getEnvironment())) {
            Location location1 = getHighestBlockAt(location);
            if (BlockUtil.isUnsafeBlock(location1.getBlock())) {
                return null;
            }
            return location1;
        } else {
            return getHighestBlockAt(location);
        }
    }

    public static CompletableFuture<Chunk> getChunkAtAsync(Location location) {
        return PaperLib.getChunkAtAsync(location);
    }

    /**
     * Check two location is equals.
     * Note: NOT check Yaw and Pitch!
     *
     * @param loc1 first location
     * @param loc2 second location
     * @return equals?
     */
    public static boolean isSameLocation(Location loc1, Location loc2) {
        return loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ() && loc1.getWorld().equals(loc2.getWorld());
    }

    /**
     * Check two location is equals.
     * Note: NOT check Yaw and Pitch!
     *
     * @param loc1            first location
     * @param loc2            second location
     * @param ignoreY         ignore Y axis
     * @param ignorePrecision ignore precision
     * @return equals?
     */
    public static boolean isSameLocation(Location loc1, Location loc2, boolean ignoreY, boolean ignorePrecision) {
        Location tmpLoc1 = loc1.clone();
        Location tmpLoc2 = loc2.clone();
        if (ignorePrecision) {
            tmpLoc1 = new Location(loc1.getWorld(), (int) loc1.getX(), (int) loc1.getX(), (int) loc1.getZ());
            tmpLoc2 = new Location(loc2.getWorld(), (int) loc2.getX(), (int) loc2.getX(), (int) loc2.getZ());
        }
        if (ignoreY) {
            tmpLoc1.setY(0);
            tmpLoc2.setY(0);
        }
        return isSameLocation(tmpLoc1, tmpLoc2);
    }
}