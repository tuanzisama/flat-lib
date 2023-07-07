package ink.tuanzi.flatlib.message;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLTokenizer {

    private static final Pattern pattern = Pattern.compile("(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?");

    private static final Pattern domainPattern = Pattern.compile("(?:https?://)?([^/]+)(/.*)?");

    /**
     * recognize URL in the input string.
     *
     * @param input a string
     */
    public static String[] serialize(String input) {
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> tokens = new ArrayList<>();
        int lastIndex = 0;
        while (matcher.find()) {
            String beforeURL = input.substring(lastIndex, matcher.start());
            if (!beforeURL.isEmpty()) {
                tokens.add(beforeURL);
            }
            tokens.add(matcher.group(0));
            lastIndex = matcher.end();
        }

        String afterURL = input.substring(lastIndex);
        if (!afterURL.isEmpty()) {
            tokens.add(afterURL);
        }

        return tokens.toArray(new String[0]);
    }

    public static boolean isDomain(String input) {
        return pattern.matcher(input).matches();
    }

    @Nullable
    public static String getDomain(String input) {
        Matcher matcher = domainPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
