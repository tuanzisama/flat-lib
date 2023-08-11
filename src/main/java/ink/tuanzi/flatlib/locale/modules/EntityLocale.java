package ink.tuanzi.flatlib.locale.modules;

import ink.tuanzi.flatlib.FlatLib;
import ink.tuanzi.flatlib.locale.AbstractLocale;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class EntityLocale extends AbstractLocale<EntityType> {

    @Override
    public File getFile() {
        return new File(FlatLib.getInstance().getDataFolder() + super.getLocaleDirPath(), "entities.yml");
    }

    @Override
    public String getConfigurationSectionPath() {
        return "entities";
    }

    @Nullable
    @Override
    public String getValueByType(EntityType typekey) {
        return getValueByType(typekey, null);
    }

    @Nullable
    @Override
    public String getValueByType(EntityType typekey, String defValue) {
        return super.getValue(typekey.name(), defValue);
    }
}
