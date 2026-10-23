package io.github.wortiiik.customtitleicon.window;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;

public final class WindowFocus {

    private WindowFocus() {
    }

    public static boolean isInForeground(long window) {
        if (GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WIN32) {
            return handle(window).equals(User32.INSTANCE.GetForegroundWindow());
        }
        return GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }

    public static HWND handle(long window) {
        return new HWND(new Pointer(GLFWNativeWin32.glfwGetWin32Window(window)));
    }
}
