package io.github.wortiiik.customtitleicon.icon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class IconSizesTest {

    @Test
    void generatesEveryStandardSizeFromLargeSource() {
        assertEquals(IconSizes.STANDARD, List.copyOf(IconSizes.generatedFrom(512)));
        assertEquals(IconSizes.STANDARD, List.copyOf(IconSizes.generatedFrom(256)));
    }

    @Test
    void neverUpscalesAndKeepsSmallerSourceSize() {
        assertEquals(List.of(16, 24, 32, 48, 64, 128, 200), List.copyOf(IconSizes.generatedFrom(200)));
        assertEquals(List.of(16, 24, 32), List.copyOf(IconSizes.generatedFrom(32)));
    }

    @Test
    void usesTinySourceAsIs() {
        assertEquals(List.of(8), List.copyOf(IconSizes.generatedFrom(8)));
    }
}
