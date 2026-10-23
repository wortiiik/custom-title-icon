package io.github.wortiiik.customtitleicon.window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class WindowStateTrackerTest {

    private static final Bounds NORMAL = new Bounds(100, 100, 1280, 720);
    private static final Bounds MAXIMIZED = new Bounds(0, 23, 1920, 1017);

    private final WindowStateTracker tracker = new WindowStateTracker();

    @Test
    void knowsNothingBeforeFirstObservation() {
        assertTrue(tracker.current().isEmpty());
    }

    @Test
    void keepsNormalBoundsWhileMaximized() {
        tracker.observe(NORMAL, false);
        tracker.observe(MAXIMIZED, true);

        assertEquals(Optional.of(new WindowState(NORMAL, true)), tracker.current());
    }

    @Test
    void followsRestoredWindow() {
        tracker.observe(NORMAL, false);
        tracker.observe(MAXIMIZED, true);
        tracker.observe(new Bounds(200, 150, 1000, 600), false);

        assertEquals(Optional.of(new WindowState(new Bounds(200, 150, 1000, 600), false)), tracker.current());
    }

    @Test
    void usesMaximizedBoundsWhenNormalOnesAreUnknown() {
        tracker.observe(MAXIMIZED, true);

        assertEquals(Optional.of(new WindowState(MAXIMIZED, true)), tracker.current());
    }

    @Test
    void ignoresEmptySizes() {
        tracker.observe(NORMAL, false);
        tracker.observe(new Bounds(0, 0, 0, 0), false);

        assertEquals(Optional.of(new WindowState(NORMAL, false)), tracker.current());
    }
}
