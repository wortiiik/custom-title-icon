package io.github.wortiiik.customtitleicon.title;

import java.util.Locale;

public record Placeholder(String text, String name, String argument) {

    public static Placeholder of(String text, String name, String argument) {
        return new Placeholder(text, name.toLowerCase(Locale.ROOT), argument == null ? "" : argument);
    }

    public boolean hasArgument() {
        return !argument.isEmpty();
    }
}
