package ink.tuanzi.flatlib.command;

import ink.tuanzi.flatlib.exception.AssertionException;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface FunctionalHandler<T extends CommandSender> {
    void onCommand(CommandContext<T> context) throws AssertionException, CommandInterruptException;

    default List<String> onTabComplete(CommandContext<T> context) throws CommandInterruptException {
        return Collections.emptyList();
    }
}
