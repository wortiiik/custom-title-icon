package io.github.wortiiik.customtitleicon.title;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class TitleVariablesTest {

    private static final TitleVariables VARIABLES = new TitleVariables(
            "Red Horizon", "2.1", "1.21.1", "21.1.264", 312, "Wortik", "New World", "RH Survival", "mc.example.org",
            modId -> modId.equals("create") ? Optional.of("6.0.4") : Optional.empty());

    @Test
    void resolvesEveryVariable() {
        assertEquals(
                "Red Horizon|2.1|1.21.1|21.1.264|312|Wortik|New World|RH Survival|mc.example.org|6.0.4",
                render("{packname}|{packversion}|{mcversion}|{neoforge}|{modcount}|{username}|{world}|{server}|{address}|{modversion:create}"));
    }

    @Test
    void resolvesHiddenAddressToEmptyText() {
        TitleVariables hidden = new TitleVariables("", "", "", "", 0, "", "", "RH Survival", "", modId -> Optional.empty());

        assertEquals("RH Survival ()", new TitleTemplate("{server} ({address})").render(hidden::resolve));
    }

    @Test
    void keepsModVersionOfMissingModAsWritten() {
        assertEquals("{modversion:missing}", render("{modversion:missing}"));
    }

    @Test
    void keepsModVersionWithoutModIdAsWritten() {
        assertEquals("{modversion} {modversion:}", render("{modversion} {modversion:}"));
    }

    @Test
    void keepsArgumentOnOtherVariablesAsWritten() {
        assertEquals("{world:nether}", render("{world:nether}"));
    }

    @Test
    void keepsUnknownVariablesAsWritten() {
        assertEquals("{biome}", render("{biome}"));
    }

    private static String render(String template) {
        return new TitleTemplate(template).render(VARIABLES::resolve);
    }
}
