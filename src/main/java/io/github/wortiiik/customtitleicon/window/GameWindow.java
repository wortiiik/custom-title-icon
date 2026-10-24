package io.github.wortiiik.customtitleicon.window;

import com.mojang.logging.LogUtils;
import io.github.wortiiik.customtitleicon.CustomTitleIcon;
import io.github.wortiiik.customtitleicon.config.ClientConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.neoforged.fml.loading.FMLPaths;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public final class GameWindow {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STATE_FILE_NAME = "window-state.json";
    private static final WindowStateTracker TRACKER = new WindowStateTracker();

    private static boolean maximizeAtStartup;

    private GameWindow() {
    }

    public static Optional<Bounds> placeAtStartup(long window, WindowSettings settings, Bounds current) {
        try {
            WindowStateFile stateFile = stateFile();
            moveLegacyStateFile(stateFile);
            Optional<WindowState> saved = settings.rememberSize() ? readState(stateFile) : Optional.empty();
            Frame frame = Frame.of(window);
            List<Bounds> workAreas = workAreas().stream().map(frame::clientArea).toList();
            Bounds primary = primaryWorkArea().map(frame::clientArea).orElse(current);
            Optional<WindowState> placement = WindowPlacement.atStartup(settings, saved, current, primary, workAreas);
            placement.ifPresent(state -> {
                TRACKER.observe(state.bounds(), false);
                maximizeAtStartup = state.maximized();
                if (GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE) {
                    GLFW.glfwRestoreWindow(window);
                }
            });
            return placement.map(WindowState::bounds);
        } catch (RuntimeException e) {
            LOGGER.warn("Could not place the game window, keeping the default size", e);
            return Optional.empty();
        }
    }

    public static boolean maximizeAtStartup() {
        return maximizeAtStartup;
    }

    public static void observe(long window, Bounds bounds) {
        if (GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_ICONIFIED) == GLFW.GLFW_TRUE) {
            return;
        }
        TRACKER.observe(bounds, GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE);
    }

    public static void saveState() {
        TRACKER.current().ifPresent(state -> {
            WindowStateFile stateFile = stateFile();
            try {
                stateFile.write(state);
            } catch (IOException e) {
                LOGGER.warn("Could not save the window size to {}", stateFile.file(), e);
            }
        });
    }

    private static void moveLegacyStateFile(WindowStateFile stateFile) {
        try {
            if (stateFile.moveLegacyFile()) {
                LOGGER.info("Moved the saved window size from {} to {}", stateFile.legacyFile(), stateFile.file());
            }
        } catch (IOException e) {
            LOGGER.warn("Could not move the saved window size from {} to {}", stateFile.legacyFile(), stateFile.file(), e);
        }
    }

    private static Optional<WindowState> readState(WindowStateFile stateFile) {
        try {
            Optional<String> json = stateFile.read();
            Optional<WindowState> state = json.flatMap(WindowState::fromJson);
            if (json.isPresent() && state.isEmpty()) {
                LOGGER.warn("Ignoring {}, it does not contain a valid window size", stateFile.file());
            }
            return state;
        } catch (IOException e) {
            LOGGER.warn("Could not read the saved window size from {}", stateFile.file(), e);
            return Optional.empty();
        }
    }

    private static WindowStateFile stateFile() {
        return new WindowStateFile(
                FMLPaths.GAMEDIR.get().resolve(CustomTitleIcon.MOD_ID).resolve(STATE_FILE_NAME),
                FMLPaths.CONFIGDIR.get().resolve(ClientConfig.ICON_FOLDER).resolve(STATE_FILE_NAME));
    }

    private static Optional<Bounds> primaryWorkArea() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        return monitor == 0L ? Optional.empty() : Optional.of(workArea(monitor));
    }

    private static List<Bounds> workAreas() {
        PointerBuffer monitors = GLFW.glfwGetMonitors();
        List<Bounds> areas = new ArrayList<>();
        if (monitors != null) {
            for (int i = 0; i < monitors.limit(); i++) {
                areas.add(workArea(monitors.get(i)));
            }
        }
        return areas;
    }

    private record Frame(int left, int top, int right, int bottom) {

        static Frame of(long window) {
            int[] left = new int[1];
            int[] top = new int[1];
            int[] right = new int[1];
            int[] bottom = new int[1];
            GLFW.glfwGetWindowFrameSize(window, left, top, right, bottom);
            return new Frame(left[0], top[0], right[0], bottom[0]);
        }

        Bounds clientArea(Bounds workArea) {
            return workArea.shrunkBy(left, top, right, bottom);
        }
    }

    private static Bounds workArea(long monitor) {
        int[] x = new int[1];
        int[] y = new int[1];
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetMonitorWorkarea(monitor, x, y, width, height);
        return new Bounds(x[0], y[0], width[0], height[0]);
    }
}
