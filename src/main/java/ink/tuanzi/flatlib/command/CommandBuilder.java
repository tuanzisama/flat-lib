package ink.tuanzi.flatlib.command;

import ink.tuanzi.flatlib.exception.AssertionException;
import ink.tuanzi.flatlib.internal.util.MsgUtil;
import lombok.Getter;
import me.lucko.helper.Commands;
import me.lucko.helper.command.Command;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Predicate;

public class CommandBuilder<T extends CommandSender> {

    protected FunctionalCommandBuilder<T> luckoCmdBuilder = (FunctionalCommandBuilder<T>) Commands.create();
    private FunctionalHandler<T> handler;

    @Getter
    private String[] cmdAliases = new String[]{};

    public CommandBuilder<T> description(String var1) {
        luckoCmdBuilder.description(var1);
        return this;
    }

    public CommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> var1) {
        luckoCmdBuilder.assertFunction(var1, (String) null);
        return this;
    }

    public CommandBuilder<T> assertPermission(String var1) {
        luckoCmdBuilder.assertPermission(var1, (String) null);
        return this;
    }

    public CommandBuilder<T> assertOp() {
        luckoCmdBuilder.assertOp("&c仅服务器管理员可执行此命令");
        return this;
    }

    public CommandBuilder<Player> assertPlayer() {
        luckoCmdBuilder.assertPlayer("&c仅普通玩家可执行此命令");
        return (CommandBuilder<Player>) this;
    }

    public CommandBuilder<ConsoleCommandSender> assertConsole() {
        luckoCmdBuilder.assertConsole("&c此命令只能通过服务器控制台使用。");
        return (CommandBuilder<ConsoleCommandSender>) this;
    }

    public CommandBuilder<CommandSender> assertConsoleOrOp() {
        luckoCmdBuilder.assertSender((sender) -> sender.isOp() || sender instanceof ConsoleCommandSender, "&c此命令仅服务器管理员或通过服务器控制台使用。");
        return (CommandBuilder<CommandSender>) this;
    }

    public CommandBuilder<CommandSender> assertConsoleOrPlayer() {
        luckoCmdBuilder.assertSender((sender) -> sender instanceof Player || sender instanceof ConsoleCommandSender, "&c此命令仅玩家或通过服务器控制台使用。");
        return (CommandBuilder<CommandSender>) this;
    }

    public CommandBuilder<T> assertUsage(String var1) {
        luckoCmdBuilder.assertUsage(var1, "&c参数无效. 用法: {usage}.");
        return this;
    }

    public CommandBuilder<T> assertArgument(int var1, Predicate<String> var2) {
        luckoCmdBuilder.assertArgument(var1, var2, "&c参数 '{arg}' 是非法的. &7({index})");
        return this;
    }

    public CommandBuilder<T> assertSender(Predicate<T> var1) {
        luckoCmdBuilder.assertSender(var1, "&c你无法使用此命令。");
        return this;
    }

    /**
     * 指令别名
     */
    public CommandBuilder<T> setCmdAliases(String... aliases) {
        this.cmdAliases = aliases;
        return this;
    }

    public CommandBuilder<T> handler(FunctionalHandler<T> var1) {
        this.handler = var1;
        return this;
    }

    public Command build() {
        Objects.requireNonNull(this.handler, "handler cannot be null!");
        return luckoCmdBuilder
                .tabHandler(commandContext -> this.handler.onTabComplete(commandContext))
                .handler(commandContext -> {
                    try {
                        this.handler.onCommand(commandContext);
                    } catch (AssertionException e) {
                        e.getAction().accept(commandContext.sender());
                    } catch (CommandInterruptException e) {
                        e.getAction().accept(commandContext.sender());
                    } catch (Exception e) {
                        MsgUtil.sendMessage(commandContext.sender(), "执行命令时出现错误，请联系管理员以获取更多帮助。");
                        e.printStackTrace();
                    }
                });
    }

}
