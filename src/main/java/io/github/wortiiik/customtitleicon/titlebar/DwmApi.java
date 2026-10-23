package io.github.wortiiik.customtitleicon.titlebar;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

interface DwmApi extends StdCallLibrary {

    DwmApi INSTANCE = Native.load("dwmapi", DwmApi.class);

    int DwmSetWindowAttribute(HWND window, int attribute, IntByReference value, int size);
}
