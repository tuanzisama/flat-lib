package ink.tuanzi.flatlib.vendor;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractPluginVendor {

    @Getter
    private JavaPlugin plugin;

    public AbstractPluginVendor(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
