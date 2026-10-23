package io.github.wortiiik.customtitleicon;

import io.github.wortiiik.customtitleicon.config.StartupConfig;
import io.github.wortiiik.customtitleicon.icon.WindowIcon;
import io.github.wortiiik.customtitleicon.titlebar.TitleBar;
import io.github.wortiiik.customtitleicon.window.Bounds;
import io.github.wortiiik.customtitleicon.window.GameWindow;
import java.util.Optional;

public final class WindowStartup {

    private static StartupConfig config;
    private static boolean appearanceApplied;

    private WindowStartup() {
    }

    public static Optional<Bounds> placeWindow(long window, Bounds current) {
        return GameWindow.placeAtStartup(window, config().window(), current);
    }

    public static boolean applyAppearanceOnce(long window) {
        if (appearanceApplied) {
            return false;
        }
        appearanceApplied = true;
        TitleBar.apply(window, config().titleBar());
        return config().iconFile().map(file -> WindowIcon.showAtStartup(window, file)).orElse(false);
    }

    private static StartupConfig config() {
        if (config == null) {
            config = StartupConfig.read();
        }
        return config;
    }
}
