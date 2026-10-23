package io.github.wortiiik.customtitleicon.icon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class IconResamplerTest {

    private static final int OPAQUE_WHITE = 0xFFFFFFFF;
    private static final int OPAQUE_BLACK = 0xFF000000;
    private static final int TRANSPARENT = 0x00000000;

    @Test
    void keepsUniformColor() {
        IconImage result = IconResampler.downscale(filled(512, 0xFF3A7BD5), 24);

        assertEquals(24, result.width());
        assertEquals(24, result.height());
        assertEquals(24 * 24, Arrays.stream(result.pixels()).filter(pixel -> pixel == 0xFF3A7BD5).count());
    }

    @Test
    void averagesHalvedBlocks() {
        IconImage checkerboard = new IconImage(2, 2, new int[] {OPAQUE_WHITE, OPAQUE_BLACK, OPAQUE_BLACK, OPAQUE_WHITE});

        assertEquals(0xFF808080, IconResampler.downscale(checkerboard, 1).pixel(0, 0));
    }

    @Test
    void transparentPixelsDoNotDarkenEdges() {
        IconImage edge = new IconImage(2, 2, new int[] {OPAQUE_WHITE, TRANSPARENT, OPAQUE_WHITE, TRANSPARENT});

        assertEquals(0x80FFFFFF, IconResampler.downscale(edge, 1).pixel(0, 0));
    }

    @Test
    void fullyTransparentAreaStaysTransparent() {
        assertEquals(TRANSPARENT, IconResampler.downscale(filled(64, 0x00FF0000), 16).pixel(5, 5));
    }

    @Test
    void weighsPartiallyCoveredPixelsByArea() {
        int[] pixels = new int[9];
        Arrays.fill(pixels, OPAQUE_BLACK);
        pixels[0] = OPAQUE_WHITE;
        IconImage source = new IconImage(3, 3, pixels);

        IconImage result = IconResampler.downscale(source, 2);

        assertEquals(0xFF717171, result.pixel(0, 0));
        assertEquals(OPAQUE_BLACK, result.pixel(1, 1));
    }

    @Test
    void keepsPixelsInPlace() {
        int[] pixels = new int[16];
        Arrays.fill(pixels, OPAQUE_BLACK);
        pixels[3] = OPAQUE_WHITE;
        IconImage source = new IconImage(4, 4, pixels);

        IconImage result = IconResampler.downscale(source, 2);

        assertEquals(0xFF404040, result.pixel(1, 0));
        assertEquals(OPAQUE_BLACK, result.pixel(0, 0));
        assertEquals(OPAQUE_BLACK, result.pixel(0, 1));
    }

    @Test
    void returnsSourceForSameSize() {
        IconImage source = filled(32, OPAQUE_WHITE);

        assertSame(source, IconResampler.downscale(source, 32));
    }

    @Test
    void rejectsUpscalingAndNonSquareSources() {
        assertThrows(IllegalArgumentException.class, () -> IconResampler.downscale(filled(16, OPAQUE_WHITE), 32));
        assertThrows(IllegalArgumentException.class, () -> IconResampler.downscale(new IconImage(2, 1, new int[2]), 1));
    }

    static IconImage filled(int size, int argb) {
        int[] pixels = new int[size * size];
        Arrays.fill(pixels, argb);
        return new IconImage(size, size, pixels);
    }
}
