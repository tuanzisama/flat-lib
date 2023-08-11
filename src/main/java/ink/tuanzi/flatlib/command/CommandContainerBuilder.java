package ink.tuanzi.flatlib.command;

import ink.tuanzi.flatlib.exception.AssertionException;
import me.lucko.helper.command.Command;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.context.ImmutableCommandContext;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 复合型指令容器构建器
 *
 * @<code> new CommandContainerBuilder().subCommands(
 * new CommandBuilder()
 * .assertPlayer()
 * .assertPermission("utolib.command.warpinfo")
 * .assertUsage("[name]")
 * .description("查看指定传送点的位置信息")
 * .setCmdAliases("info") // 用以注册子指令
 * .handler(new WarpInfoHandler())
 * ).register("waa"); // 主指令
 * </code>
 */
public class CommandContainerBuilder<T extends CommandSender> extends CommandBuilder<T> {

    private CommandBuilder[] commandBuilders;

    public CommandContainerBuilder() {

    }

    public Command subCommands(CommandBuilder... builders) {
        this.commandBuilders = builders;

        List<String> tabListKeywords = new ArrayList<>();
        for (int i = 0; i < builders.length; i++) {
            tabListKeywords.addAll(Arrays.stream(builders[i].getCmdAliases()).toList());
        }

        return luckoCmdBuilder
                .tabHandler(commandContext -> {
                    if (commandContext.args().size() <= 1) {
                        return tabListKeywords;
                    } else {
                        String subCommand = commandContext.arg(0).parseOrFail(String.class);
                        CommandBuilder builder = getCommandBuilderByAlias(subCommand);
                        if (builder != null) {
                            return builder.build().callTabCompleter(sliceCommandArgs(builder, commandContext));
                        }
                    }
                    return Collections.emptyList();
                })
                .handler(commandContext -> {
                    try {
                        String subCommand = commandContext.arg(0).parse(String.class).orElse("");
                        AssertionException.makeAssertion(!subCommand.isBlank(), "此子指令不存在");

                        CommandBuilder builder = getCommandBuilderByAlias(subCommand);
                        AssertionException.makeAssertion(builder != null, "此子指令不存在");

                        builder.build().call(sliceCommandArgs(builder, commandContext));
                    } catch (Exception e) {
                        if (e instanceof AssertionException) {
                            ((AssertionException) e).getAction().accept(commandContext.sender());
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Nullable
    private CommandBuilder getCommandBuilderByAlias(String alias) {
        CommandBuilder builder = null;
        for (int i = 0; i < this.commandBuilders.length; i++) {
            CommandBuilder tmpBuilder = this.commandBuilders[i];
            boolean contains = Arrays.stream(tmpBuilder.getCmdAliases()).toList().contains(alias);
            if (contains) {
                builder = tmpBuilder;
                break;
            }
        }
        return builder;
    }

    private CommandContext<T> sliceCommandArgs(CommandBuilder builder, CommandContext<T> context) {
        List<String> args = context.args().subList(1, context.args().size());
        String[] subList = args.toArray(new String[args.size()]);
        CommandContext<T> extendCtx = new ImmutableCommandContext<>(context.sender(), context.label(), subList, Arrays.stream(builder.getCmdAliases()).toList());
        return extendCtx;
    }
}
