package io.github.wortiiik.customtitleicon.window;

import com.mojang.logging.LogUtils;
import io.github.wortiiik.customtitleicon.config.ClientConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.neoforged.fml.loading.FMLPaths;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public final class GameWindow {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STATE_FILE = "window-state.json";
    private static final WindowStateTracker TRACKER = new WindowStateTracker();

    private static boolean maximizeAtStartup;

    private GameWindow() {
    }

    public static Optional<Bounds> placeAtStartup(long window, WindowSettings settings, Bounds current) {
        try {
            Optional<WindowState> saved = settings.rememberSize() ? readState() : Optional.empty();
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
            Path file = stateFile();
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, state.toJson(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.warn("Could not save the window size to {}", file, e);
            }
        });
    }

    private static Optional<WindowState> readState() {
        Path file = stateFile();
        try {
            Optional<WindowState> state = WindowState.fromJson(Files.readString(file, StandardCharsets.UTF_8));
            if (state.isEmpty()) {
                LOGGER.warn("Ignoring {}, it does not contain a valid window size", file);
            }
            return state;
        } catch (NoSuchFileException e) {
            return Optional.empty();
        } catch (IOException e) {
            LOGGER.warn("Could not read the saved window size from {}", file, e);
            return Optional.empty();
        }
    }

    private static Path stateFile() {
        return FMLPaths.CONFIGDIR.get().resolve(ClientConfig.ICON_FOLDER).resolve(STATE_FILE);
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
