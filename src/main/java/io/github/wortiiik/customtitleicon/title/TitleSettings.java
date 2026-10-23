package io.github.wortiiik.customtitleicon.title;

import java.util.LinkedHashSet;
import java.util.Set;

public record TitleSettings(
        String packName,
        String packVersion,
        TitleTemplate menu,
        TitleTemplate singleplayer,
        TitleTemplate multiplayer,
        boolean showServerAddress) {

    public TitleTemplate templateFor(GameState state) {
        TitleTemplate template = switch (state) {
            case MENU -> menu;
            case SINGLEPLAYER -> singleplayer;
            case MULTIPLAYER -> multiplayer;
        };
        return template.isBlank() ? menu : template;
    }

    public Set<Placeholder> placeholders() {
        Set<Placeholder> placeholders = new LinkedHashSet<>(menu.placeholders());
        placeholders.addAll(singleplayer.placeholders());
        placeholders.addAll(multiplayer.placeholders());
        return placeholders;
    }
}
