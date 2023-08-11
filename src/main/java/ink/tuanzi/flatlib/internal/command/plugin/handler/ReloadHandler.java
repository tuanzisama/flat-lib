package ink.tuanzi.flatlib.internal.command.plugin.handler;

import ink.tuanzi.flatlib.FlatLib;
import ink.tuanzi.flatlib.command.FunctionalHandler;
import ink.tuanzi.flatlib.exception.AssertionException;
import ink.tuanzi.flatlib.internal.util.MsgUtil;
import ink.tuanzi.flatlib.locale.LocaleManager;
import ink.tuanzi.flatlib.message.MessageBuilder;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadHandler implements FunctionalHandler<CommandSender> {


    @Override
    public void onCommand(CommandContext<CommandSender> context) throws AssertionException {
        String type = context.arg(0).parse(String.class).orElseThrow(() -> new AssertionException(String.format("&e使用方法: &6/%s reload [locale]", context.label())));
        String pluginName = FlatLib.getInstance().getName();
        try {
            switch (type) {
                case "locale" -> {
                    LocaleManager.reload();
                    MsgUtil.sendMessage(context.sender(), new MessageBuilder().success().append("已重载").append(pluginName).append("语言文件").build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> onTabComplete(CommandContext<CommandSender> context) throws CommandInterruptException {
        if (context.args().size() == 1) {
            return List.of("locale");
        }
        return Collections.emptyList();
    }
}
