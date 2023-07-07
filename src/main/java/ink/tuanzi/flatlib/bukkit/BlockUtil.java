package ink.tuanzi.flatlib.bukkit;

import org.bukkit.block.Block;

public class BlockUtil extends MaterialUtil {
    public static boolean isAir(Block block) {
        return isAir(block.getType());
    }

    public static boolean isUnsafeBlock(Block block) {
        return isUnsafe(block.getType());
    }

    public static boolean isShulkerBox(Block block) {
        return isShulkerBox(block.getType());
    }
}
