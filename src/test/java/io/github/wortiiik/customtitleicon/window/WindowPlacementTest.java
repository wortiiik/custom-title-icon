package io.github.wortiiik.customtitleicon.window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WindowPlacementTest {

    private static final Bounds PRIMARY = new Bounds(0, 0, 1920, 1040);
    private static final Bounds SECOND = new Bounds(1920, 0, 2560, 1400);
    private static final List<Bounds> BOTH = List.of(PRIMARY, SECOND);
    private static final Bounds FML_WINDOW = new Bounds(533, 280, 854, 480);

    @Test
    void keepsDefaultWindowWithoutSettings() {
        assertEquals(Optional.empty(), place(new WindowSettings(0, 0, false, false), Optional.empty(), BOTH));
    }

    @Test
    void centersInitialSizeOnPrimaryMonitor() {
        WindowState state = place(new WindowSettings(1600, 900, false, false), Optional.empty(), BOTH).orElseThrow();

        assertEquals(new Bounds(160, 70, 1600, 900), state.bounds());
        assertFalse(state.maximized());
    }

    @Test
    void keepsDefaultForUnsetDimension() {
        WindowState state = place(new WindowSettings(1280, 0, false, false), Optional.empty(), BOTH).orElseThrow();

        assertEquals(1280, state.bounds().width());
        assertEquals(480, state.bounds().height());
    }

    @Test
    void shrinksInitialSizeToFitPrimaryMonitor() {
        WindowState state = place(new WindowSettings(4000, 3000, false, false), Optional.empty(), BOTH).orElseThrow();

        assertEquals(PRIMARY, state.bounds());
    }

    @Test
    void maximizesDefaultWindow() {
        WindowState state = place(new WindowSettings(0, 0, true, false), Optional.empty(), BOTH).orElseThrow();

        assertEquals(FML_WINDOW, state.bounds());
        assertTrue(state.maximized());
    }

    @Test
    void restoresSavedStateOverInitialSettings() {
        WindowState saved = new WindowState(new Bounds(2100, 200, 1280, 720), true);

        WindowState state = place(new WindowSettings(1600, 900, false, true), Optional.of(saved), BOTH).orElseThrow();

        assertEquals(saved, state);
    }

    @Test
    void centersSavedWindowOnPrimaryWhenItsMonitorIsGone() {
        WindowState saved = new WindowState(new Bounds(2100, 200, 1280, 720), false);

        WindowState state = place(new WindowSettings(0, 0, false, true), Optional.of(saved), List.of(PRIMARY)).orElseThrow();

        assertEquals(new Bounds(320, 160, 1280, 720), state.bounds());
    }

    @Test
    void shrinksSavedWindowToFitItsMonitor() {
        WindowState saved = new WindowState(new Bounds(2000, 300, 1600, 32000), false);

        WindowState state = place(new WindowSettings(0, 0, false, true), Optional.of(saved), BOTH).orElseThrow();

        assertEquals(new Bounds(2000, 0, 1600, 1400), state.bounds());
    }

    @Test
    void ignoresSavedStateWhenRememberingIsOff() {
        WindowState saved = new WindowState(new Bounds(2100, 200, 1280, 720), false);

        assertEquals(Optional.empty(), place(new WindowSettings(0, 0, false, false), Optional.of(saved), BOTH));
    }

    @Test
    void findsMonitorUnderTopEdgeCenter() {
        assertEquals(Optional.of(SECOND), WindowPlacement.workAreaUnderTopEdge(new Bounds(1500, 100, 1000, 600), BOTH));
        assertEquals(Optional.empty(), WindowPlacement.workAreaUnderTopEdge(new Bounds(100, -400, 800, 600), BOTH));
        assertEquals(Optional.empty(), WindowPlacement.workAreaUnderTopEdge(new Bounds(-1500, 100, 800, 600), BOTH));
    }

    private static Optional<WindowState> place(WindowSettings settings, Optional<WindowState> saved, List<Bounds> workAreas) {
        return WindowPlacement.atStartup(settings, saved, FML_WINDOW, PRIMARY, workAreas);
    }
}
