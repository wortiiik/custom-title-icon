package io.github.wortiiik.customtitleicon.window;

import java.util.List;
import java.util.Optional;

public final class WindowPlacement {

    private WindowPlacement() {
    }

    public static Optional<WindowState> atStartup(
            WindowSettings settings,
            Optional<WindowState> saved,
            Bounds current,
            Bounds primaryWorkArea,
            List<Bounds> workAreas) {
        if (settings.rememberSize() && saved.isPresent()) {
            return Optional.of(restore(saved.get(), primaryWorkArea, workAreas));
        }
        boolean resize = settings.initialWidth() > 0 || settings.initialHeight() > 0;
        if (!resize && !settings.startMaximized()) {
            return Optional.empty();
        }
        Bounds bounds = resize ? initialBounds(settings, current).centeredIn(primaryWorkArea) : current;
        return Optional.of(new WindowState(bounds, settings.startMaximized()));
    }

    public static Optional<Bounds> workAreaUnderTopEdge(Bounds window, List<Bounds> workAreas) {
        int topCenterX = window.x() + window.width() / 2;
        return workAreas.stream().filter(area -> area.contains(topCenterX, window.y())).findFirst();
    }

    private static WindowState restore(WindowState saved, Bounds primaryWorkArea, List<Bounds> workAreas) {
        Bounds bounds = workAreaUnderTopEdge(saved.bounds(), workAreas)
                .map(saved.bounds()::fittedInto)
                .orElseGet(() -> saved.bounds().centeredIn(primaryWorkArea));
        return new WindowState(bounds, saved.maximized());
    }

    private static Bounds initialBounds(WindowSettings settings, Bounds current) {
        return current.withSize(
                settings.initialWidth() > 0 ? settings.initialWidth() : current.width(),
                settings.initialHeight() > 0 ? settings.initialHeight() : current.height());
    }
}
