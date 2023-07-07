package ink.tuanzi.flatlib.text;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern parsePattern = Pattern.compile("&#[a-fA-F0-9]{6}");
    private static final Pattern colorParsePattern = Pattern.compile("&([a-fA-F0-9]|#[a-fA-F0-9]{6})");
    private static final Pattern stringifyPattern = Pattern.compile(ChatColor.COLOR_CHAR + "x(" + ChatColor.COLOR_CHAR + "[a-fA-F0-9]){6}");
    private static final Set<ChatColor> styleColors = Set.of(
            ChatColor.MAGIC,
            ChatColor.BOLD,
            ChatColor.STRIKETHROUGH,
            ChatColor.UNDERLINE,
            ChatColor.ITALIC,
            ChatColor.RESET
    );

    /**
     * 颜色转义 & -> §
     *
     * @param text 文字
     * @return
     */
    public static String parse(String text) {
        Matcher matcher = parsePattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, formatHexColor(color));
            matcher = parsePattern.matcher(text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * 颜色反转 § -> &
     *
     * @param text
     * @return
     */
    public static String stringify(String text) {
        Matcher matcher = stringifyPattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, unformatHexColor(color));
            matcher = stringifyPattern.matcher(text);
        }
        return StringUtils.replace(text, "" + ChatColor.COLOR_CHAR, "&");
    }


    /**
     * 判断一个字符串是否包含转义字符
     *
     * @param text 欲判断的字符串
     * @return 是否包含转义字符
     */
    public static boolean contains(String text) {
        return colorParsePattern.matcher(text).find();
    }

    /**
     * 去除字符串中的颜色字符
     *
     * @param text 欲去除的字符串
     * @return 去除后的字符串
     */
    public static String purge(String text) {
        return text.replaceAll(colorParsePattern.pattern(), "");
    }

    /**
     * 去除字符串中的闪烁字符 “&m”
     *
     * @param text 欲去除的字符串
     * @return 去除后的字符串
     */
    public static String purgeMagic(String text) {
        return text.replaceAll(ChatColor.MAGIC.name(), "");
    }

    /**
     * 去除字符串中的&[lLnNoOkKmMrR]
     *
     * @param text 欲去除的字符串
     * @return 去除后的字符串
     */
    public static String purgeStyles(String text) {
        for (ChatColor styleColor : styleColors) {
            return text.replaceAll(styleColor.name(), "");
        }
        return text;
    }

    private static String formatHexColor(String color) {
        if (!color.startsWith("&#")) return color;
        String rtnColor = "";
        for (char co : color.substring(2).toCharArray()) {
            rtnColor += "&" + co;
        }
        return "&x" + rtnColor;
    }

    private static String unformatHexColor(String color) {
        if (!StringUtils.startsWithIgnoreCase(color, ChatColor.COLOR_CHAR + "x")) return color;
        return color.replace("" + ChatColor.COLOR_CHAR, "").replaceAll("[Xx]", ChatColor.COLOR_CHAR + "#");
    }
}
