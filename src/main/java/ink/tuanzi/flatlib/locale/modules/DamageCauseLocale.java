package ink.tuanzi.flatlib.locale.modules;

import ink.tuanzi.flatlib.FlatLib;
import ink.tuanzi.flatlib.locale.AbstractLocale;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class DamageCauseLocale extends AbstractLocale<EntityDamageEvent.DamageCause> {

    @Override
    public File getFile() {
        return new File(FlatLib.getInstance().getDataFolder() + super.getLocaleDirPath(), "damage-causes.yml");
    }

    @Override
    public String getConfigurationSectionPath() {
        return "damage-causes";
    }

    @Nullable
    @Override
    public String getValueByType(EntityDamageEvent.DamageCause typekey) {
        return getValueByType(typekey, null);
    }

    @Nullable
    @Override
    public String getValueByType(EntityDamageEvent.DamageCause typekey, String defValue) {
        return super.getValue(typekey.name(), defValue);
    }

    @Deprecated
    public String format(EntityDamageEvent.DamageCause typeKey, @NotNull String... entitiesName) {
        return getValueAsFormatted(typeKey, entitiesName);
    }

    public String getValueAsFormatted(EntityDamageEvent.DamageCause typeKey, @NotNull String... entitiesName) {
        String i18n = getValueByType(typeKey, "");
        for (int i = 0; i < entitiesName.length; i++) {
            i18n = i18n.replaceAll("%" + (i + 1), entitiesName[i]);
        }
        return i18n;
    }
}
