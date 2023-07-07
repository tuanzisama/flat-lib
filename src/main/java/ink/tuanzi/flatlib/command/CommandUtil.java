package ink.tuanzi.flatlib.command;

import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandUtil {

    /**
     * Execute a single command.
     *
     * <p>
     * Execute as a SERVER CONSOLE prefixed with "server:". Usually this will not be restricted when executed, message receipts may not be returned correctly to the triggering player.
     * Execute as a SERVER OPERATOR prefixed with "op:". This will temporarily give this player an operator privilege.
     * cmdText will automatically remove slash on first position.
     * </p>
     *
     * @param player  the player who triggered.
     * @param cmdText a command.
     */
    public static void execute(Player player, String cmdText) {
        Promise.start().thenRunSync(() -> {
            String commands = cmdText;
            if (commands.toLowerCase().startsWith("server:")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commands.substring(7));
            } else if (commands.toLowerCase().startsWith("op:")) {
                String command = commands.substring(3);
                if (player.isOp()) {
                    player.performCommand(command);
                } else {
                    try {
                        player.setOp(true);
                        player.performCommand(command);
                    } catch (Throwable error) {
                        error.printStackTrace();
                    } finally {
                        player.setOp(false);
                    }
                }
            } else {
                if (commands.startsWith("/")) {
                    commands = commands.replaceFirst("/", "");
                }
                player.performCommand(commands);
            }
        });
    }

    /**
     * Execute a command set
     *
     * <p>For more information, see {@link this#execute}.</p>
     *
     * @param player       the player who triggered.
     * @param cmdList      a command set.
     * @param delay        instruction execution delay
     * @param intervalUnit time unit
     */
    public static void executeBatch(Player player, List<String> cmdList, int delay, @Nonnull TimeUnit intervalUnit) {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Scheduler scheduler = Schedulers.sync();
        scheduler.runRepeating((task) -> {
            int index = atomicInteger.getAndAdd(1);
            if (index >= cmdList.size()) {
                task.close();
                return;
            }
            String command = cmdList.get(index);
            execute(player, command);
        }, 0, TimeUnit.SECONDS, delay, intervalUnit);
    }
}
