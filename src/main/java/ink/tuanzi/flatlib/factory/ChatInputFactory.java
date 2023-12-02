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
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * 聊天栏输入工厂类
 */
public class ChatInputFactory {
    private BiPredicate<Player, String> callbackHandler = null;
    private BiConsumer<Player, FailedReason> failedHandler = null;
    private final List<SingleSubscription<? extends Event>> eventHandlers = new ArrayList<>();

    public ChatInputFactory handler(BiPredicate<Player, String> callbackHandler) {
        this.callbackHandler = callbackHandler;
        return this;
    }

    public ChatInputFactory failed(BiConsumer<Player, FailedReason> failedHandler) {
        this.failedHandler = failedHandler;
        return this;
    }

    public void toPlayer(Player player) {
        this.eventHandlers.add(Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> player.equals(e.getPlayer()))
                .handler(e -> {
                    e.setCancelled(true);
                    Schedulers.sync().run(() -> {
                        boolean isSuccess = this.callbackHandler.test(player, e.getMessage());
                        if (isSuccess) {
                            this.close();
                        }
                    });
                }));

        this.eventHandlers.add(Events.subscribe(PlayerQuitEvent.class)
                .filter(e -> player.equals(e.getPlayer()))
                .handler(e -> {
                    this.close();
                    if (this.failedHandler != null) {
                        this.failedHandler.accept(player, FailedReason.PLAYER_QUIT);
                    }
                }));

        this.eventHandlers.add(Events.subscribe(PlayerCommandPreprocessEvent.class, EventPriority.HIGHEST)
                .filter(e -> player.equals(e.getPlayer()))
                .handler(e -> {
                    e.setCancelled(true);
                    if (this.failedHandler != null) {
                        this.failedHandler.accept(player, FailedReason.RUN_COMMAND);
                    }
                }));
    }

    public void close() {
        for (SingleSubscription<? extends Event> eventHandler : this.eventHandlers) {
            eventHandler.unregister();
        }
    }

    public enum FailedReason {
        RUN_COMMAND,
        PLAYER_QUIT,
    }
}
