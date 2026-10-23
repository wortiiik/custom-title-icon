package io.github.wortiiik.customtitleicon.window;

public record WindowSettings(int initialWidth, int initialHeight, boolean startMaximized, boolean rememberSize) {

    public static final WindowSettings DEFAULT = new WindowSettings(0, 0, false, false);
}
