package ink.tuanzi.flatlib.internal.command;

import ink.tuanzi.flatlib.internal.command.plugin.PluginCommandsModule;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer terminableConsumer) {
        terminableConsumer.bindModule(new PluginCommandsModule());
    }
}