package ink.tuanzi.flatlib.message;

import de.themoep.minedown.MineDown;
import ink.tuanzi.flatlib.FlatLib;
import ink.tuanzi.flatlib.text.ColorUtil;
import ink.tuanzi.flatlib.vendor.AbstractPluginVendor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class MessageHelper extends AbstractPluginVendor {

    @Getter
    @Setter
    private List<String> messagePrefix = List.of(this.getPlugin().getName());

    private MessageHelper() {
        super(FlatLib.getInstance());
    }

    private MessageHelper(JavaPlugin plugin) {
        super(plugin);
    }

    public static MessageHelper create(JavaPlugin plugin) {
        return new MessageHelper(plugin);
    }

    private BaseComponent[] getRandomPrefix() {
        String prefix = getMessagePrefix().get(RandomUtils.nextInt(0, getMessagePrefix().size()));
        BaseComponent[] baseComponents = new ComponentBuilder("")
                .append(MineDown.parse(prefix), ComponentBuilder.FormatRetention.NONE)
                .create();
        return baseComponents;
    }

    private BaseComponent[] getContentWithPrefix(@NotNull BaseComponent... contentComponents) {
        return new ComponentBuilder("")
                .append(getRandomPrefix())
                .append(contentComponents, ComponentBuilder.FormatRetention.NONE)
                .create();
    }

    public <T extends CommandSender> void sendMessage(T commandSender, @NotNull String message) {
        sendMessage(commandSender, new TextComponent(message));
    }

    public <T extends CommandSender> void sendMessage(T commandSender, @NotNull BaseComponent... contentComponents) {
        if (commandSender instanceof Player) {
            sendMessage((Player) commandSender, ChatMessageType.SYSTEM, contentComponents);
        } else {
            commandSender.spigot().sendMessage(contentComponents);
        }
    }

    public <T extends Player> void sendMessage(T sender, @NotNull ChatMessageType messageType, @NotNull String message) {
        sendMessage(sender, messageType, new TextComponent(message));
    }

    public <T extends Player> void sendMessage(T sender, @NotNull ChatMessageType messageType, @NotNull BaseComponent... contentComponents) {
        BaseComponent[] components = getContentWithPrefix(contentComponents);
        sender.spigot().sendMessage(messageType, components);
    }

    public <T extends Player> void broadcast(@NotNull List<T> playerList, @NotNull BaseComponent... contentComponents) {
        broadcast(playerList, ChatMessageType.SYSTEM, contentComponents);
    }

    public <T extends Player> void broadcast(@NotNull List<T> playerList, @NotNull ChatMessageType messageType, @NotNull BaseComponent... contentComponents) {
        BaseComponent[] components = getContentWithPrefix(contentComponents);
        playerList.forEach(player -> player.spigot().sendMessage(messageType, components));
    }

    public <T extends Player> void sendTitle(T player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(ColorUtil.parse(title), ColorUtil.parse(subtitle), fadeIn, stay, fadeOut);
    }

    public <T extends Player> void sendActionBar(T player, BaseComponent... components) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    public void printToConsole(Level level, String message) {
        this.getPlugin().getLogger().log(level, ColorUtil.parse(message));
    }
}
