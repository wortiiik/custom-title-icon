package io.github.wortiiik.customtitleicon.title;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class TitleSettingsTest {

    @Test
    void picksTemplateForEachState() {
        TitleSettings settings = settings("Menu", "Solo", "Online");

        assertEquals("Menu", settings.templateFor(GameState.MENU).source());
        assertEquals("Solo", settings.templateFor(GameState.SINGLEPLAYER).source());
        assertEquals("Online", settings.templateFor(GameState.MULTIPLAYER).source());
    }

    @Test
    void usesDefaultTemplateWhenStateTemplateIsBlank() {
        TitleSettings settings = settings("Menu", "", "  ");

        assertEquals("Menu", settings.templateFor(GameState.SINGLEPLAYER).source());
        assertEquals("Menu", settings.templateFor(GameState.MULTIPLAYER).source());
    }

    @Test
    void collectsPlaceholdersFromAllTemplates() {
        TitleSettings settings = settings("{packname}", "{world}", "{server} {packname}");

        List<String> names = settings.placeholders().stream().map(Placeholder::name).toList();

        assertEquals(List.of("packname", "world", "server"), names);
    }

    private static TitleSettings settings(String menu, String singleplayer, String multiplayer) {
        return new TitleSettings("", "", new TitleTemplate(menu), new TitleTemplate(singleplayer), new TitleTemplate(multiplayer), false);
    }
}
