package io.github.wortiiik.customtitleicon.titlebar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TitleBarColorTest {

    @Test
    void convertsToColorRefWithBlueInHighByte() {
        assertEquals(0x00563412, TitleBarColor.toColorRef("#123456"));
        assertEquals(0x000000FF, TitleBarColor.toColorRef("#FF0000"));
        assertEquals(0x0000FF00, TitleBarColor.toColorRef("#00FF00"));
        assertEquals(0x00FF0000, TitleBarColor.toColorRef("#0000FF"));
    }

    @Test
    void acceptsLowercaseAndSurroundingSpaces() {
        assertEquals(0x00EFCDAB, TitleBarColor.toColorRef("  #abcdef "));
    }

    @Test
    void usesSystemDefaultForEmptyOrInvalidValues() {
        assertEquals(TitleBarColor.SYSTEM_DEFAULT, TitleBarColor.toColorRef(""));
        assertEquals(TitleBarColor.SYSTEM_DEFAULT, TitleBarColor.toColorRef("#12345"));
        assertEquals(TitleBarColor.SYSTEM_DEFAULT, TitleBarColor.toColorRef("123456"));
        assertEquals(TitleBarColor.SYSTEM_DEFAULT, TitleBarColor.toColorRef("#GGGGGG"));
        assertEquals(TitleBarColor.SYSTEM_DEFAULT, TitleBarColor.toColorRef("#1234567"));
    }

    @Test
    void validatesConfigValues() {
        assertTrue(TitleBarColor.isValid(""));
        assertTrue(TitleBarColor.isValid("#1E1E2E"));
        assertFalse(TitleBarColor.isValid("red"));
        assertFalse(TitleBarColor.isValid("#1E1E2"));
    }
}
