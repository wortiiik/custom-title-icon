package io.github.wortiiik.customtitleicon.window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WindowStateFileTest {

    private static final String PLAYER_STATE = "{\"x\":10,\"y\":20,\"width\":1280,\"height\":720,\"maximized\":false}";
    private static final String PACK_STATE = "{\"x\":-1700,\"y\":100,\"width\":1600,\"height\":900,\"maximized\":true}";

    @TempDir
    Path gameFolder;

    private WindowStateFile stateFile;

    @BeforeEach
    void createStateFile() {
        stateFile = new WindowStateFile(
                gameFolder.resolve("customtitleicon").resolve("window-state.json"),
                gameFolder.resolve("config").resolve("customtitleicon").resolve("window-state.json"));
    }

    @Test
    void movesLegacyFileToNewPlace() throws IOException {
        writeLegacy(PLAYER_STATE);

        assertTrue(stateFile.moveLegacyFile());

        assertFalse(Files.exists(stateFile.legacyFile()));
        assertEquals(Optional.of(PLAYER_STATE), stateFile.read());
    }

    @Test
    void keepsExistingStateAndDeletesLegacyFile() throws IOException {
        stateFile.write(WindowState.fromJson(PLAYER_STATE).orElseThrow());
        writeLegacy(PACK_STATE);

        assertFalse(stateFile.moveLegacyFile());

        assertFalse(Files.exists(stateFile.legacyFile()));
        assertEquals(WindowState.fromJson(PLAYER_STATE), stateFile.read().flatMap(WindowState::fromJson));
    }

    @Test
    void leavesOtherFilesInLegacyFolderAlone() throws IOException {
        writeLegacy(PLAYER_STATE);
        Path icon = stateFile.legacyFile().resolveSibling("icon.png");
        Files.writeString(icon, "png");

        stateFile.moveLegacyFile();

        assertTrue(Files.exists(icon));
    }

    @Test
    void doesNothingWithoutLegacyFile() throws IOException {
        assertFalse(stateFile.moveLegacyFile());
        assertEquals(Optional.empty(), stateFile.read());
    }

    @Test
    void writesStateThatCanBeReadBack() throws IOException {
        WindowState state = new WindowState(new Bounds(5, 6, 800, 600), true);

        stateFile.write(state);

        assertEquals(Optional.of(state), stateFile.read().flatMap(WindowState::fromJson));
    }

    private void writeLegacy(String json) throws IOException {
        Files.createDirectories(stateFile.legacyFile().getParent());
        Files.writeString(stateFile.legacyFile(), json, StandardCharsets.UTF_8);
    }
}
