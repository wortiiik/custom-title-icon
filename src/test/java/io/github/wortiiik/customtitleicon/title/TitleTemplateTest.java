package io.github.wortiiik.customtitleicon.title;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class TitleTemplateTest {

    private static final Function<Placeholder, Optional<String>> VALUES = placeholder ->
            Optional.ofNullable(Map.of("packname", "Red Horizon", "packversion", "2.1", "price", "$5").get(placeholder.name()));

    @Test
    void replacesKnownVariables() {
        assertEquals("Red Horizon 2.1", new TitleTemplate("{packname} {packversion}").render(VALUES));
    }

    @Test
    void keepsUnknownVariablesAsWritten() {
        assertEquals("Red Horizon {nothing}", new TitleTemplate("{packname} {nothing}").render(VALUES));
    }

    @Test
    void matchesVariableNamesIgnoringCase() {
        assertEquals("Red Horizon", new TitleTemplate("{PackName}").render(VALUES));
    }

    @Test
    void leavesTextThatIsNotAVariableAlone() {
        assertEquals("{ packname } {} {pack name}", new TitleTemplate("{ packname } {} {pack name}").render(VALUES));
    }

    @Test
    void insertsValuesWithSpecialCharactersLiterally() {
        assertEquals("Costs $5", new TitleTemplate("Costs {price}").render(VALUES));
    }

    @Test
    void readsArgumentAfterColon() {
        List<Placeholder> placeholders = List.copyOf(new TitleTemplate("{modversion:create}").placeholders());

        assertEquals(List.of(new Placeholder("{modversion:create}", "modversion", "create")), placeholders);
    }

    @Test
    void listsEachPlaceholderOnce() {
        assertEquals(1, new TitleTemplate("{world} - {world}").placeholders().size());
    }

    @Test
    void blankTemplateHasNoPlaceholders() {
        TitleTemplate template = new TitleTemplate("   ");

        assertTrue(template.isBlank());
        assertTrue(template.placeholders().isEmpty());
    }
}
