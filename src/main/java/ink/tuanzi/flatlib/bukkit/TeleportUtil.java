package ink.tuanzi.flatlib.bukkit;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;

public class TeleportUtil {

    public static <T extends Entity> CompletableFuture<Boolean> teleportAsync(T entity, Location location) {
        return teleportAsync(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public static <T extends Entity> CompletableFuture<Boolean> teleportAsync(T entity, Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return PaperLib.teleportAsync(entity, location);
    }
}
