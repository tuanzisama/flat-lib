package ink.tuanzi.flatlib.locale.modules;

import ink.tuanzi.flatlib.FlatLib;
import ink.tuanzi.flatlib.locale.AbstractLocale;
import org.bukkit.Material;

import java.io.File;

public class ItemLocale extends AbstractLocale<Material> {
    @Override
    public String getValueByType(Material typekey) {
        return getValueByType(typekey, null);
    }

    @Override
    public String getValueByType(Material typekey, String defValue) {
        return getValue(typekey.name(), defValue);
    }

    @Override
    public String getConfigurationSectionPath() {
        return "items";
    }

    @Override
    public File getFile() {
        return new File(FlatLib.getInstance().getDataFolder() + super.getLocaleDirPath(), "items.yml");
    }
}
