package ink.tuanzi.flatlib.locale;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface ILocale<T> {
    File getFile();

    void load();

    String getConfigurationSectionPath();

    /**
     * 获取翻译值
     *
     * @param key KEY
     * @return 中文翻译值
     */
    @Nullable
    String getValue(String key);


    /**
     * 获取翻译值
     *
     * @param key      KEY
     * @param defValue 默认值
     * @return 中文翻译值
     */
    @Nullable
    String getValue(String key, String defValue);


    /**
     * 根据自定义类型返回中文翻译值
     *
     * @return 中文翻译值
     */
    @Nullable
    default String getValueByType(T typekey) {
        return null;
    }

    /**
     * 根据自定义类型返回中文翻译值
     *
     * @param defValue 默认值
     * @return 中文翻译值
     */
    @Nullable
    default String getValueByType(T typekey, String defValue) {
        return null;
    }
}
