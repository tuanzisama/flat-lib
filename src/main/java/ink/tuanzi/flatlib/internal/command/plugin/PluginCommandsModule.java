package ink.tuanzi.flatlib.internal.command.plugin;

import ink.tuanzi.flatlib.command.CommandBuilder;
import ink.tuanzi.flatlib.command.CommandContainerBuilder;
import ink.tuanzi.flatlib.internal.command.plugin.handler.ReloadHandler;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class PluginCommandsModule implements TerminableModule {

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        new CommandContainerBuilder().subCommands(
                new CommandBuilder()
                        .assertConsoleOrOp()
                        .assertPermission("flatlib.command.reload")
                        .assertUsage("[type]")
                        .description("重载配置")
                        .setCmdAliases("reload")
                        .handler(new ReloadHandler())
        ).register("flatlib", "flib");
    }
}
