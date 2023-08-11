package ink.tuanzi.flatlib.factory;

import ink.tuanzi.flatlib.internal.util.MsgUtil;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.event.SingleSubscription;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 聊天栏输入工厂类
 * <code>
 * new ChatInputFactory(Bukkit.getPlayer("1"))
 * .handler(str -> {
 * // do somethings...
 * }).start().close();
 * </code>
 */
public class ChatInputFactory {
    private static final Map<Player, Predicate<String>> queueList = new HashMap<>();
    private final Player player;
    private final List<SingleSubscription<? extends Event>> eventHandlers = new ArrayList<>();

    public ChatInputFactory(Player player) {
        this.player = player;
    }

    public ChatInputFactory handler(Predicate<String> consumer) {
        ChatInputFactory.queueList.put(player, consumer);
        return this;
    }

    public ChatInputFactory start() {
        this.eventHandlers.add(Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> queueList.containsKey(e.getPlayer()))
                .handler(e -> {
                    e.setCancelled(true);
                    Schedulers.sync().run(() -> {
                        Predicate<String> predicate = queueList.get(e.getPlayer());
                        boolean isSuccess = predicate.test(e.getMessage());
                        if (isSuccess) {
                            this.close();
                        }
                    });
                }));

        this.eventHandlers.add(Events.subscribe(PlayerQuitEvent.class)
                .filter(e -> queueList.containsKey(e.getPlayer()))
                .handler(e -> this.close()));

        this.eventHandlers.add(Events.subscribe(PlayerCommandPreprocessEvent.class, EventPriority.HIGHEST)
                .filter(e -> queueList.containsKey(e.getPlayer()))
                .handler(e -> {
                    MsgUtil.sendMessage(e.getPlayer(), "请先输入内容");
                    e.setCancelled(true);
                }));

        return this;
    }

    public void close() {
        ChatInputFactory.queueList.remove(player);
        for (SingleSubscription<? extends Event> eventHandler : this.eventHandlers) {
            eventHandler.unregister();
        }
    }
}
