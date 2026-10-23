package io.github.wortiiik.customtitleicon.titlebar;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

final class WindowsRegistry {

    private static final String CURRENT_VERSION = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
    private static final String PERSONALIZE = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";

    private WindowsRegistry() {
    }

    static int buildNumber() {
        try {
            return Integer.parseInt(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, CURRENT_VERSION, "CurrentBuildNumber").strip());
        } catch (Win32Exception | NumberFormatException e) {
            return 0;
        }
    }

    static boolean appsUseDarkTheme() {
        try {
            return Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, PERSONALIZE, "AppsUseLightTheme") == 0;
        } catch (Win32Exception e) {
            return false;
        }
    }
}
