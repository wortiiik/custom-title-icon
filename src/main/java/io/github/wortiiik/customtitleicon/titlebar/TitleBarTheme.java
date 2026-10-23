package io.github.wortiiik.customtitleicon.titlebar;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum TitleBarTheme {
    SYSTEM,
    LIGHT,
    DARK;

    public static final List<String> CONFIG_VALUES = Arrays.stream(values()).map(TitleBarTheme::configValue).toList();

    public String configValue() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static TitleBarTheme fromConfig(String value) {
        String normalized = value.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(theme -> theme.configValue().equals(normalized))
                .findFirst()
                .orElse(SYSTEM);
    }
}
