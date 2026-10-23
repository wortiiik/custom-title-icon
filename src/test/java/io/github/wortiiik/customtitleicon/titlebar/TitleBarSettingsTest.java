package io.github.wortiiik.customtitleicon.titlebar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TitleBarSettingsTest {

    @Test
    void readsThemeIgnoringCase() {
        assertEquals(TitleBarTheme.DARK, TitleBarSettings.parse("Dark", "", "", "").theme());
        assertEquals(TitleBarTheme.LIGHT, TitleBarSettings.parse(" light ", "", "", "").theme());
    }

    @Test
    void fallsBackToSystemTheme() {
        assertEquals(TitleBarTheme.SYSTEM, TitleBarSettings.parse("", "", "", "").theme());
        assertEquals(TitleBarTheme.SYSTEM, TitleBarSettings.parse("purple", "", "", "").theme());
    }

    @Test
    void keepsEachColorInItsPlace() {
        TitleBarSettings settings = TitleBarSettings.parse("system", "#010203", "#040506", "#070809");

        assertEquals(0x00030201, settings.captionColor());
        assertEquals(0x00060504, settings.textColor());
        assertEquals(0x00090807, settings.borderColor());
    }

    @Test
    void reportsWhetherAnyColorIsSet() {
        assertFalse(TitleBarSettings.DEFAULT.hasColors());
        assertTrue(TitleBarSettings.parse("system", "", "", "#FFFFFF").hasColors());
    }

    @Test
    void listsConfigValuesInLowercase() {
        assertEquals(List.of("system", "light", "dark"), TitleBarTheme.CONFIG_VALUES);
    }
}
