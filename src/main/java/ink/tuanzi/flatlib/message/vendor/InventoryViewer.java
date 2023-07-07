package ink.tuanzi.flatlib.message.vendor;

import ink.tuanzi.flatlib.text.ColorUtil;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.event.filter.EventFilters;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InventoryViewer {

    @Getter
    private static final ConcurrentHashMap<UUID, Inventory> invMap = new ConcurrentHashMap<>();

    private static final String identifier = ColorUtil.parse(" &#c6c6c6INV_VIEWER");

    static {
        Events.subscribe(InventoryInteractEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e -> {
                    if (e.getView().getTitle().endsWith(identifier)) {
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                    }
                });

        Events.subscribe(InventoryClickEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.ignoreCancelled())
                .handler(e -> {
                    if (e.getView().getTitle().endsWith(identifier)) {
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                    }
                });
    }

    public static UUID putShulkerBox(ShulkerBox shulkerBox, String title) {
        UUID uuid = UUID.randomUUID();

        Inventory copyInventory = shulkerBox.getInventory();
        Inventory newInventory = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, title + identifier);
        newInventory.setContents(copyInventory.getContents().clone());

        putInvMap(uuid, newInventory);
        return uuid;
    }

    public static UUID putPlayerInventory(Player player) {
        UUID uuid = UUID.randomUUID();

        Inventory copyInventory = player.getInventory();
        Inventory newInventory = Bukkit.createInventory(null, InventoryType.PLAYER, String.format("%s 的背包", player.getName()) + identifier);
        newInventory.setContents(copyInventory.getContents().clone());

        putInvMap(uuid, newInventory);
        return uuid;
    }

    public static boolean open(Player player, UUID uuid) {
        boolean flag = invMap.containsKey(uuid);
        if (flag) {
            Inventory inventory = invMap.get(uuid);
            player.openInventory(inventory);
        }
        return flag;
    }

    private static void putInvMap(UUID uuid, Inventory inventory) {
        invMap.put(uuid, inventory);

        Schedulers.sync().runLater(() -> {
            invMap.remove(uuid, inventory);
        }, 5, TimeUnit.MINUTES);
    }
}
