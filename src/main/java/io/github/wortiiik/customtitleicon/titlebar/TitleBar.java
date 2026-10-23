package io.github.wortiiik.customtitleicon.titlebar;

import com.mojang.logging.LogUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import io.github.wortiiik.customtitleicon.window.WindowFocus;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public final class TitleBar {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int USE_IMMERSIVE_DARK_MODE = 20;
    private static final int USE_IMMERSIVE_DARK_MODE_BEFORE_20H1 = 19;
    private static final int BORDER_COLOR = 34;
    private static final int CAPTION_COLOR = 35;
    private static final int TEXT_COLOR = 36;
    private static final int FIRST_DARK_MODE_BUILD = 17763;
    private static final int FIRST_COLOR_BUILD = 22000;
    private static final int REDRAW_FRAME_ONLY = WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOZORDER
            | WinUser.SWP_NOACTIVATE | WinUser.SWP_FRAMECHANGED;

    private static TitleBarSettings settings;
    private static int windowsBuild = -1;
    private static boolean darkApplied;
    private static boolean failed;
    private static boolean colorsUnsupportedReported;

    private TitleBar() {
    }

    public static void apply(long window, TitleBarSettings newSettings) {
        settings = newSettings;
        update(window);
    }

    public static void reapply(long window) {
        if (settings != null) {
            update(window);
        }
    }

    public static void onFocusGained(long window) {
        if (settings != null && settings.theme() == TitleBarTheme.SYSTEM && !failed && isSupported()
                && WindowsRegistry.appsUseDarkTheme() != darkApplied) {
            update(window);
        }
    }

    private static void update(long window) {
        if (failed || !isSupported()) {
            return;
        }
        try {
            HWND handle = WindowFocus.handle(window);
            darkApplied = isDark(settings.theme());
            setDarkMode(handle, darkApplied);
            applyColors(handle);
            User32.INSTANCE.SetWindowPos(handle, null, 0, 0, 0, 0, REDRAW_FRAME_ONLY);
        } catch (RuntimeException | LinkageError e) {
            failed = true;
            LOGGER.warn("Could not change the title bar, leaving the system default", e);
        }
    }

    private static boolean isSupported() {
        if (windowsBuild < 0) {
            windowsBuild = GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WIN32 ? WindowsRegistry.buildNumber() : 0;
        }
        return windowsBuild >= FIRST_DARK_MODE_BUILD;
    }

    private static boolean isDark(TitleBarTheme theme) {
        return switch (theme) {
            case SYSTEM -> WindowsRegistry.appsUseDarkTheme();
            case LIGHT -> false;
            case DARK -> true;
        };
    }

    private static void setDarkMode(HWND window, boolean dark) {
        int value = dark ? 1 : 0;
        if (!setAttribute(window, USE_IMMERSIVE_DARK_MODE, value)) {
            setAttribute(window, USE_IMMERSIVE_DARK_MODE_BEFORE_20H1, value);
        }
    }

    private static void applyColors(HWND window) {
        if (windowsBuild >= FIRST_COLOR_BUILD) {
            setAttribute(window, CAPTION_COLOR, settings.captionColor());
            setAttribute(window, TEXT_COLOR, settings.textColor());
            setAttribute(window, BORDER_COLOR, settings.borderColor());
        } else if (settings.hasColors() && !colorsUnsupportedReported) {
            colorsUnsupportedReported = true;
            LOGGER.info("Title bar colors need Windows 11, only the theme is applied");
        }
    }

    private static boolean setAttribute(HWND window, int attribute, int value) {
        return DwmApi.INSTANCE.DwmSetWindowAttribute(window, attribute, new IntByReference(value), Integer.BYTES) >= 0;
    }
}
