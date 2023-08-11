package ink.tuanzi.flatlib.locale;

import ink.tuanzi.flatlib.locale.modules.*;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class LocaleManager implements TerminableModule {

    public static EntityLocale entity;
    public static ItemLocale item;
    public static DamageCauseLocale damageCause;

    @Override
    public void setup(@NotNull TerminableConsumer terminableConsumer) {
        loadConfig();
    }

    private static void loadConfig() {
        LocaleManager.item = new ItemLocale();
        LocaleManager.item.load();

        LocaleManager.entity = new EntityLocale();
        LocaleManager.entity.load();

        LocaleManager.damageCause = new DamageCauseLocale();
        LocaleManager.damageCause.load();
    }

    public static void reload() {
        loadConfig();
    }

}
