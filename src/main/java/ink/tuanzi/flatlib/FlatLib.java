package ink.tuanzi.flatlib;

import ink.tuanzi.flatlib.internal.command.CommandManager;
import ink.tuanzi.flatlib.locale.LocaleManager;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class FlatLib extends ExtendedJavaPlugin {

    @Getter
    private static FlatLib instance;

    @Override
    protected void enable() {
        instance = this;

        bindModule(new LocaleManager());
        bindModule(new CommandManager());
    }
}
