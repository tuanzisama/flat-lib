package ink.tuanzi.flatlib.message;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum MessageLevel {
    // Support Minedown color syntax
    INFO("&#EFFAF9&"),
    SUCCESS("&#19BE6B&"),
    NONE(ChatColor.RESET + ChatColor.WHITE.toString()),
    WARN(ChatColor.RED.toString());

    @Getter
    private String chatColor;

    MessageLevel(String color) {
        this.chatColor = color;
    }
}
