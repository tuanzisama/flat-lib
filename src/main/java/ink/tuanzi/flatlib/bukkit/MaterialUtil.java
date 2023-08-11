package ink.tuanzi.flatlib.bukkit;

import org.bukkit.Material;

import java.util.Set;

public class MaterialUtil {
    private static final Set<Material> airMaterials = Set.of(Material.AIR, Material.AIR, Material.VOID_AIR);
    private static final Set<Material> unsafeMaterials = Set.of(Material.WATER, Material.LAVA, Material.VOID_AIR, Material.MAGMA_BLOCK, Material.FIRE, Material.SOUL_FIRE, Material.POWDER_SNOW);

    public static boolean isAir(Material material) {
        return airMaterials.contains(material);
    }

    public static boolean isUnsafe(Material material) {
        return unsafeMaterials.contains(material);
    }

    public static boolean isShulkerBox(Material material) {
        return material.name().endsWith("shulker_box");
    }

    public static boolean isSign(Material material) {
        return material.name().endsWith("_sign");
    }
}
