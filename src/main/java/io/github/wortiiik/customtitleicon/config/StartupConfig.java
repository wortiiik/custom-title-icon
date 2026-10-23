package io.github.wortiiik.customtitleicon.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import io.github.wortiiik.customtitleicon.titlebar.TitleBarSettings;
import io.github.wortiiik.customtitleicon.window.WindowSettings;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import net.neoforged.fml.loading.FMLPaths;

public record StartupConfig(Optional<String> iconFile, TitleBarSettings titleBar, WindowSettings window) {

    private static final StartupConfig MISSING = new StartupConfig(Optional.empty(), TitleBarSettings.DEFAULT, WindowSettings.DEFAULT);

    public static StartupConfig read() {
        try (Reader reader = Files.newBufferedReader(FMLPaths.CONFIGDIR.get().resolve(ClientConfig.FILE_NAME), StandardCharsets.UTF_8)) {
            return from(TomlFormat.instance().createParser().parse(reader));
        } catch (IOException | RuntimeException e) {
            return MISSING;
        }
    }

    private static StartupConfig from(UnmodifiableConfig config) {
        return new StartupConfig(
                string(config, ClientConfig.ICON, ClientConfig.ICON_FILE),
                TitleBarSettings.parse(
                        string(config, ClientConfig.TITLE_BAR, ClientConfig.THEME).orElse(""),
                        string(config, ClientConfig.TITLE_BAR, ClientConfig.CAPTION_COLOR).orElse(""),
                        string(config, ClientConfig.TITLE_BAR, ClientConfig.TEXT_COLOR).orElse(""),
                        string(config, ClientConfig.TITLE_BAR, ClientConfig.BORDER_COLOR).orElse("")),
                new WindowSettings(
                        size(config, ClientConfig.INITIAL_WIDTH),
                        size(config, ClientConfig.INITIAL_HEIGHT),
                        flag(config, ClientConfig.START_MAXIMIZED),
                        flag(config, ClientConfig.REMEMBER_SIZE)));
    }

    private static int size(UnmodifiableConfig config, String key) {
        return config.get(List.of(ClientConfig.WINDOW, key)) instanceof Number number ? Math.max(0, number.intValue()) : 0;
    }

    private static boolean flag(UnmodifiableConfig config, String key) {
        return config.get(List.of(ClientConfig.WINDOW, key)) instanceof Boolean value && value;
    }

    private static Optional<String> string(UnmodifiableConfig config, String section, String key) {
        return config.get(List.of(section, key)) instanceof String value ? Optional.of(value) : Optional.empty();
    }
}
