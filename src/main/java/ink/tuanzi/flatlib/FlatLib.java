package ink.tuanzi.flatlib;

import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class FlatLib extends ExtendedJavaPlugin {

    @Getter
    private static FlatLib instance;

    @Override
    protected void enable() {
        instance = this;
    }
}
