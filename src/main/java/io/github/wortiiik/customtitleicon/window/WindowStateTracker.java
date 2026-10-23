package io.github.wortiiik.customtitleicon.window;

import java.util.Optional;

public final class WindowStateTracker {

    private Bounds normalBounds;
    private boolean maximized;

    public void observe(Bounds bounds, boolean isMaximized) {
        if (!bounds.hasArea()) {
            return;
        }
        if (!isMaximized || normalBounds == null) {
            normalBounds = bounds;
        }
        maximized = isMaximized;
    }

    public Optional<WindowState> current() {
        return Optional.ofNullable(normalBounds).map(bounds -> new WindowState(bounds, maximized));
    }
}
