package ink.tuanzi.flatlib.bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class ItemStackUtil extends MaterialUtil {
    public static boolean isAir(ItemStack itemStack) {
        return isAir(itemStack.getType());
    }

    public static boolean isUnsafeBlock(ItemStack itemStack) {
        return isUnsafe(itemStack.getType());
    }

    public static boolean isShulkerBox(ItemStack itemStack) {
        return isShulkerBox(itemStack.getType());
    }

    @Nullable
    public static String getDisplayName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        return itemMeta.getDisplayName();
    }
}
