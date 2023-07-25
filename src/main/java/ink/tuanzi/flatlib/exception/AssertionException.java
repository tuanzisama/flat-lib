package ink.tuanzi.flatlib.exception;

import ink.tuanzi.flatlib.internal.util.MsgUtil;
import ink.tuanzi.flatlib.message.MessageBuilder;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public class AssertionException extends Exception {

    @Getter
    private final Consumer<CommandSender> action;

    public AssertionException(Consumer<CommandSender> action) {
        this.action = action;
    }

    public AssertionException(String message) {
        this.action = (cs) -> MsgUtil.sendMessage(cs, new MessageBuilder().warn().append(message).build());
    }

    public AssertionException(BaseComponent[] message) {
        this.action = (cs) -> MsgUtil.sendMessage(cs, message);
    }


    /**
     * Makes an assertion about a condition.
     *
     * <p>When used inside a command, command processing will be gracefully halted
     * if the condition is not true.</p>
     *
     * @param condition the condition
     * @param failMsg   the message to send to the player if the assertion fails
     * @throws AssertionException if the assertion fails
     */
    public static void makeAssertion(boolean condition, String failMsg) throws AssertionException {
        if (!condition) throw new AssertionException(failMsg);
    }

    public static void makeAssertion(boolean condition, BaseComponent... msgComponents) throws AssertionException {
        if (!condition) throw new AssertionException(msgComponents);
    }
}
