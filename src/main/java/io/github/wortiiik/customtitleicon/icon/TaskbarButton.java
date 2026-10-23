package io.github.wortiiik.customtitleicon.icon;

import com.mojang.logging.LogUtils;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import io.github.wortiiik.customtitleicon.window.WindowFocus;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

final class TaskbarButton {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Guid.CLSID TASKBAR_LIST = new Guid.CLSID("56FDF344-FD6D-11d0-958A-006097C9A090");
    private static final Guid.IID TASKBAR_LIST_INTERFACE = new Guid.IID("56FDF342-FD6D-11d0-958A-006097C9A090");

    private static boolean failed;

    private TaskbarButton() {
    }

    static void refreshInBackground(long glfwWindow) {
        if (failed || GLFW.glfwGetPlatform() != GLFW.GLFW_PLATFORM_WIN32) {
            return;
        }
        try {
            if (!WindowFocus.isInForeground(glfwWindow)) {
                recreate(WindowFocus.handle(glfwWindow));
            }
        } catch (RuntimeException | LinkageError e) {
            failed = true;
            LOGGER.warn("Could not refresh the taskbar button, the new icon shows there once the window is activated", e);
        }
    }

    private static void recreate(HWND window) {
        boolean initialized = COMUtils.SUCCEEDED(Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED));
        try {
            PointerByReference instance = new PointerByReference();
            if (COMUtils.SUCCEEDED(Ole32.INSTANCE.CoCreateInstance(TASKBAR_LIST, null, WTypes.CLSCTX_INPROC_SERVER, TASKBAR_LIST_INTERFACE, instance))) {
                TaskbarList taskbar = new TaskbarList(instance.getValue());
                try {
                    taskbar.recreateTab(window);
                } finally {
                    taskbar.Release();
                }
            }
        } finally {
            if (initialized) {
                Ole32.INSTANCE.CoUninitialize();
            }
        }
    }

    private static final class TaskbarList extends Unknown {

        private static final int HR_INIT = 3;
        private static final int ADD_TAB = 4;
        private static final int DELETE_TAB = 5;

        TaskbarList(Pointer pointer) {
            super(pointer);
        }

        void recreateTab(HWND window) {
            if (COMUtils.SUCCEEDED(_invokeNativeInt(HR_INIT, new Object[] {getPointer()}))) {
                _invokeNativeInt(DELETE_TAB, new Object[] {getPointer(), window});
                _invokeNativeInt(ADD_TAB, new Object[] {getPointer(), window});
            }
        }
    }
}
