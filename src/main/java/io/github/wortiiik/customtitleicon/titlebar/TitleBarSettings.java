package io.github.wortiiik.customtitleicon.titlebar;

public record TitleBarSettings(TitleBarTheme theme, int captionColor, int textColor, int borderColor) {

    public static final TitleBarSettings DEFAULT = parse("", "", "", "");

    public static TitleBarSettings parse(String theme, String captionColor, String textColor, String borderColor) {
        return new TitleBarSettings(
                TitleBarTheme.fromConfig(theme),
                TitleBarColor.toColorRef(captionColor),
                TitleBarColor.toColorRef(textColor),
                TitleBarColor.toColorRef(borderColor));
    }

    public boolean hasColors() {
        return captionColor != TitleBarColor.SYSTEM_DEFAULT
                || textColor != TitleBarColor.SYSTEM_DEFAULT
                || borderColor != TitleBarColor.SYSTEM_DEFAULT;
    }
}
