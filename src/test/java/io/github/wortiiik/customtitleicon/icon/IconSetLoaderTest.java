package io.github.wortiiik.customtitleicon.icon;

import static io.github.wortiiik.customtitleicon.icon.IconResamplerTest.filled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class IconSetLoaderTest {

    private static final Path ICON = Path.of("config", "customtitleicon", "icon.png");
    private static final int GENERATED = 0xFF112233;
    private static final int HAND_DRAWN = 0xFFAABBCC;

    private final Map<Path, IconImage> files = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();
    private final IconSetLoader loader = new IconSetLoader(this::read, warnings::add);

    @Test
    void generatesStandardSizesFromSource() throws IOException {
        files.put(ICON, filled(512, GENERATED));

        List<IconImage> icons = loader.load(ICON);

        assertEquals(IconSizes.STANDARD, icons.stream().map(IconImage::width).toList());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void prefersHandDrawnSizes() throws IOException {
        files.put(ICON, filled(512, GENERATED));
        files.put(ICON.resolveSibling("icon-16.png"), filled(16, HAND_DRAWN));

        List<IconImage> icons = loader.load(ICON);

        assertEquals(HAND_DRAWN, icons.getFirst().pixel(0, 0));
        assertEquals(GENERATED, icons.get(1).pixel(0, 0));
    }

    @Test
    void addsHandDrawnSizeLargerThanSource() throws IOException {
        files.put(ICON, filled(64, GENERATED));
        files.put(ICON.resolveSibling("icon-256.png"), filled(256, HAND_DRAWN));

        List<IconImage> icons = loader.load(ICON);

        assertEquals(List.of(16, 24, 32, 48, 64, 256), icons.stream().map(IconImage::width).toList());
    }

    @Test
    void generatesSizeWhenHandDrawnFileHasWrongSize() throws IOException {
        files.put(ICON, filled(256, GENERATED));
        files.put(ICON.resolveSibling("icon-32.png"), filled(30, HAND_DRAWN));

        IconImage icon32 = loader.load(ICON).get(2);

        assertEquals(GENERATED, icon32.pixel(0, 0));
        assertEquals(1, warnings.size());
    }

    @Test
    void rejectsNonSquareSource() {
        files.put(ICON, new IconImage(512, 256, new int[512 * 256]));

        assertThrows(InvalidIconException.class, () -> loader.load(ICON));
    }

    @Test
    void reportsMissingSource() {
        assertThrows(NoSuchFileException.class, () -> loader.load(ICON));
    }

    @Test
    void namesHandDrawnFilesAfterSource() {
        assertEquals(Path.of("icons", "pack-48.png"), IconSetLoader.handDrawnPath(Path.of("icons", "pack.png"), 48));
        assertEquals(Path.of("pack.v2-16.png"), IconSetLoader.handDrawnPath(Path.of("pack.v2.png"), 16));
        assertEquals(Path.of("pack-16"), IconSetLoader.handDrawnPath(Path.of("pack"), 16));
    }

    private IconImage read(Path file) throws IOException {
        IconImage image = files.get(file);
        if (image == null) {
            throw new NoSuchFileException(file.toString());
        }
        return image;
    }
}
