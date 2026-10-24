package io.github.wortiiik.customtitleicon.window;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public record WindowStateFile(Path file, Path legacyFile) {

    public boolean moveLegacyFile() throws IOException {
        if (!Files.exists(legacyFile)) {
            return false;
        }
        if (Files.exists(file)) {
            Files.delete(legacyFile);
            return false;
        }
        Files.createDirectories(file.getParent());
        Files.move(legacyFile, file);
        return true;
    }

    public Optional<String> read() throws IOException {
        try {
            return Optional.of(Files.readString(file, StandardCharsets.UTF_8));
        } catch (NoSuchFileException e) {
            return Optional.empty();
        }
    }

    public void write(WindowState state) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, state.toJson(), StandardCharsets.UTF_8);
    }
}
