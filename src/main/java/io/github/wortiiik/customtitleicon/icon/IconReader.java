package io.github.wortiiik.customtitleicon.icon;

import java.io.IOException;
import java.nio.file.Path;

@FunctionalInterface
public interface IconReader {

    IconImage read(Path file) throws IOException;
}
