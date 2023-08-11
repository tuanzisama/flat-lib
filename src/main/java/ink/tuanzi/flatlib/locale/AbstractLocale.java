package ink.tuanzi.flatlib.locale;

import ink.tuanzi.flatlib.FlatLib;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;

public abstract class AbstractLocale<T> implements ILocale<T> {
    @Getter
    private ConfigurationSection configurationSection;

    @Getter
    private final String localeDirPath = "/locale";

    @Nullable
    @Override
    public String getValue(String key) {
        return getValue(key, null);
    }

    @Nullable
    @Override
    public String getValue(String key, String defValue) {
        return getConfigurationSection().getString(key, defValue);
    }

    public void load() {
        YamlConfiguration defaultYaml = YamlConfiguration.loadConfiguration(
                new InputStreamReader(
                        Objects.requireNonNull(getFileStream()),
                        StandardCharsets.UTF_8
                )
        );

        File file = getFile();
        if (!file.exists()) save(true);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.options().copyDefaults(false);
        cfg.setDefaults(defaultYaml);

        try {
            cfg.save(file);
            configurationSection = cfg.getConfigurationSection(getConfigurationSectionPath());
        } catch (IOException e) {
            FlatLib.getInstance().getLogger().log(Level.WARNING, "Could not load/save transaction locale from " + getFile().getName() + ". Skipping...", e);
        }

    }

    public void save(boolean replace) {
        FlatLib.getInstance().saveResource(getResourcePath(), replace);
    }

    public InputStream getFileStream() {
        return FlatLib.getInstance().getResource(getResourcePath());
    }

    private String getResourcePath() {
        return StringUtils.stripStart(localeDirPath, "/") + "/" + getFile().getName();
    }
}
