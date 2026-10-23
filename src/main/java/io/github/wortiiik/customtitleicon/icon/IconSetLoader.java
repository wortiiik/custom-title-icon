package io.github.wortiiik.customtitleicon.icon;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public final class IconSetLoader {

    private final IconReader reader;
    private final Consumer<String> warnings;

    public IconSetLoader(IconReader reader, Consumer<String> warnings) {
        this.reader = reader;
        this.warnings = warnings;
    }

    public List<IconImage> load(Path iconFile) throws IOException {
        IconImage source = reader.read(iconFile);
        if (!source.isSquare()) {
            throw new InvalidIconException("the image is " + source.width() + "x" + source.height() + ", it must be square");
        }
        SortedMap<Integer, IconImage> icons = new TreeMap<>();
        for (int size : IconSizes.STANDARD) {
            handDrawn(iconFile, size).ifPresent(icon -> icons.put(size, icon));
        }
        for (int size : IconSizes.generatedFrom(source.width())) {
            icons.computeIfAbsent(size, missing -> IconResampler.downscale(source, missing));
        }
        return List.copyOf(icons.values());
    }

    public static Path handDrawnPath(Path iconFile, int size) {
        String name = iconFile.getFileName().toString();
        int extension = name.lastIndexOf('.');
        String variant = extension > 0
                ? name.substring(0, extension) + "-" + size + name.substring(extension)
                : name + "-" + size;
        return iconFile.resolveSibling(variant);
    }

    private Optional<IconImage> handDrawn(Path iconFile, int size) {
        Path file = handDrawnPath(iconFile, size);
        try {
            IconImage icon = reader.read(file);
            if (icon.width() == size && icon.height() == size) {
                return Optional.of(icon);
            }
            warnings.accept(file + " is " + icon.width() + "x" + icon.height() + " instead of " + size + "x" + size + ", generating that size instead");
        } catch (NoSuchFileException e) {
            return Optional.empty();
        } catch (IOException e) {
            warnings.accept(file + " could not be read (" + e.getMessage() + "), generating that size instead");
        }
        return Optional.empty();
    }
}
