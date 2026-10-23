package io.github.wortiiik.customtitleicon.title;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record TitleTemplate(String source) {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z]+)(?::([^{}]*))?}");

    public boolean isBlank() {
        return source.isBlank();
    }

    public String render(Function<Placeholder, Optional<String>> values) {
        return PLACEHOLDER.matcher(source).replaceAll(match -> {
            Placeholder placeholder = placeholderOf(match);
            return Matcher.quoteReplacement(values.apply(placeholder).orElse(placeholder.text()));
        });
    }

    public Set<Placeholder> placeholders() {
        Set<Placeholder> placeholders = new LinkedHashSet<>();
        PLACEHOLDER.matcher(source).results().map(TitleTemplate::placeholderOf).forEach(placeholders::add);
        return placeholders;
    }

    private static Placeholder placeholderOf(MatchResult match) {
        return Placeholder.of(match.group(), match.group(1), match.group(2));
    }
}
