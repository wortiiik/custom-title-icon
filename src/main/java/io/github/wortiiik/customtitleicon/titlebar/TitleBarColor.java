package io.github.wortiiik.customtitleicon.titlebar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TitleBarColor {

    public static final int SYSTEM_DEFAULT = 0xFFFFFFFF;

    private static final Pattern HEX = Pattern.compile("#([0-9A-Fa-f]{6})");

    private TitleBarColor() {
    }

    public static boolean isValid(String value) {
        return value.isBlank() || HEX.matcher(value.strip()).matches();
    }

    public static int toColorRef(String value) {
        Matcher matcher = HEX.matcher(value.strip());
        if (!matcher.matches()) {
            return SYSTEM_DEFAULT;
        }
        int rgb = Integer.parseInt(matcher.group(1), 16);
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        return blue << 16 | green << 8 | red;
    }
}
