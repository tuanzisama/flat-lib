package ink.tuanzi.flatlib.internal.util;

import ink.tuanzi.flatlib.FlatLib;
import ink.tuanzi.flatlib.message.MessageHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class MsgUtil {

    private static final MessageHelper helper = MessageHelper.create(FlatLib.getInstance());

    static {
        helper.setMessagePrefix(List.of("<FlatLib> "));
    }

    public static <T extends CommandSender> void sendMessage(@NotNull T commandSender, @NotNull String message) {
        helper.sendMessage(commandSender, message);
    }

    public static <T extends CommandSender> void sendMessage(@NotNull T commandSender, @NotNull BaseComponent... components) {
        helper.sendMessage(commandSender, components);
    }

    public static <T extends Player> void sendTitle(@NotNull T sender, String title, String subtitles) {
        helper.sendTitle(sender, title, subtitles, 10, 70, 20);
    }

    public static <T extends Player> void broadcast(@NotNull ChatMessageType messageType, @NotNull BaseComponent... contentComponents) {
        helper.broadcast(Bukkit.getOnlinePlayers().stream().toList(), contentComponents);
    }

    public static void printToConsole(String message) {
        helper.sendMessage(Bukkit.getConsoleSender(), new TextComponent(message));
    }

    public static void printToConsole(Level level, String message) {
        helper.printToConsole(level, message);
    }
}
