package io.github.wortiiik.customtitleicon.window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class WindowStateTest {

    @Test
    void survivesJsonRoundTrip() {
        WindowState state = new WindowState(new Bounds(-1200, 40, 1600, 900), true);

        assertEquals(Optional.of(state), WindowState.fromJson(state.toJson()));
    }

    @Test
    void treatsMissingMaximizedAsFalse() {
        WindowState state = WindowState.fromJson("{\"x\":1,\"y\":2,\"width\":300,\"height\":200}").orElseThrow();

        assertEquals(new WindowState(new Bounds(1, 2, 300, 200), false), state);
    }

    @Test
    void rejectsBrokenOrIncompleteFiles() {
        assertTrue(WindowState.fromJson("").isEmpty());
        assertTrue(WindowState.fromJson("{ not json").isEmpty());
        assertTrue(WindowState.fromJson("[]").isEmpty());
        assertTrue(WindowState.fromJson("{\"x\":1,\"y\":2,\"width\":300}").isEmpty());
        assertTrue(WindowState.fromJson("{\"x\":1,\"y\":2,\"width\":\"wide\",\"height\":200}").isEmpty());
        assertTrue(WindowState.fromJson("{\"x\":1,\"y\":2,\"width\":0,\"height\":200}").isEmpty());
    }
}
