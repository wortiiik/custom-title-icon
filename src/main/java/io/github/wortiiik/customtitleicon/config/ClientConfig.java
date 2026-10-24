package io.github.wortiiik.customtitleicon.config;

import io.github.wortiiik.customtitleicon.CustomTitleIcon;
import io.github.wortiiik.customtitleicon.title.TitleSettings;
import io.github.wortiiik.customtitleicon.title.TitleTemplate;
import io.github.wortiiik.customtitleicon.titlebar.TitleBarColor;
import io.github.wortiiik.customtitleicon.titlebar.TitleBarSettings;
import io.github.wortiiik.customtitleicon.titlebar.TitleBarTheme;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public final class ClientConfig {

    public static final String FILE_NAME = CustomTitleIcon.MOD_ID + "-client.toml";
    public static final String ICON_FOLDER = CustomTitleIcon.MOD_ID;
    public static final String DEFAULT_ICON_FILE = ICON_FOLDER + "/icon.png";

    static final String ICON = "icon";
    static final String ICON_FILE = "file";
    static final String TITLE_BAR = "titlebar";
    static final String THEME = "theme";
    static final String CAPTION_COLOR = "caption_color";
    static final String TEXT_COLOR = "text_color";
    static final String BORDER_COLOR = "border_color";
    static final String WINDOW = "window";
    static final String INITIAL_WIDTH = "initial_width";
    static final String INITIAL_HEIGHT = "initial_height";
    static final String START_MAXIMIZED = "start_maximized";
    static final String REMEMBER_SIZE = "remember_size";

    private static final String PACK = "pack";
    private static final String TITLE = "title";
    private static final int MAX_WINDOW_SIZE = 16384;

    private final ModConfigSpec spec;
    private final ConfigValue<String> packName;
    private final ConfigValue<String> packVersion;
    private final ConfigValue<String> menuTitle;
    private final ConfigValue<String> singleplayerTitle;
    private final ConfigValue<String> multiplayerTitle;
    private final BooleanValue showServerAddress;
    private final ConfigValue<String> iconFile;
    private final ConfigValue<String> theme;
    private final ConfigValue<String> captionColor;
    private final ConfigValue<String> textColor;
    private final ConfigValue<String> borderColor;
    private final BooleanValue flashWhenLoaded;
    private final BooleanValue flashOnDisconnect;
    private final IntValue initialWidth;
    private final IntValue initialHeight;
    private final BooleanValue startMaximized;
    private final BooleanValue rememberSize;

    public ClientConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Modpack details, used by the {packname} and {packversion} title variables.")
                .translation(translationKey(PACK))
                .push(PACK);
        packName = builder
                .comment("Name of the modpack.")
                .translation(translationKey(PACK, "name"))
                .define("name", "");
        packVersion = builder
                .comment("Version of the modpack.")
                .translation(translationKey(PACK, "version"))
                .define("version", "");
        builder.pop();

        builder.comment(
                        "Window title for each game state.",
                        "Variables: {packname}, {packversion}, {mcversion}, {neoforge}, {modcount}, {username},",
                        "{world} (singleplayer world name), {server} (server name from the server list),",
                        "{address} (server address, see show_server_address), {modversion:<modid>}.",
                        "Unknown variables are shown as written.")
                .translation(translationKey(TITLE))
                .push(TITLE);
        menuTitle = builder
                .comment("Title in the menus. Leave empty to keep the vanilla title.")
                .translation(translationKey(TITLE, "default"))
                .define("default", "Minecraft {mcversion}");
        singleplayerTitle = builder
                .comment("Title in a singleplayer world, including one opened to LAN. Empty uses the default title.")
                .translation(translationKey(TITLE, "singleplayer"))
                .define("singleplayer", "");
        multiplayerTitle = builder
                .comment("Title on a server or in a LAN world of another player. Empty uses the default title.")
                .translation(translationKey(TITLE, "multiplayer"))
                .define("multiplayer", "");
        showServerAddress = builder
                .comment("Fill {address} with the server address. When off, {address} is empty, which is safer for streaming.")
                .translation(translationKey(TITLE, "show_server_address"))
                .define("show_server_address", false);
        builder.pop();

        builder.comment("Window icon. Not supported on macOS and Wayland, where the setting is ignored.")
                .translation(translationKey(ICON))
                .push(ICON);
        iconFile = builder
                .comment(
                        "Square PNG with transparency, relative to the config folder. 256x256 or 512x512 is recommended.",
                        "Sizes 16, 24, 32, 48, 64, 128 and 256 are generated from it.",
                        "A file next to it named like icon-16.png is used for that size instead of a generated one.",
                        "Leave empty to keep the vanilla icon.")
                .translation(translationKey(ICON, ICON_FILE))
                .define(ICON_FILE, DEFAULT_ICON_FILE);
        builder.pop();

        builder.comment("Title bar look on Windows. Ignored on other systems.")
                .translation(translationKey(TITLE_BAR))
                .push(TITLE_BAR);
        theme = builder
                .comment(
                        "system follows the Windows setting for apps (light or dark), light and dark force one.",
                        "Needs Windows 10 version 1809 or newer.")
                .translation(translationKey(TITLE_BAR, THEME))
                .defineInList(THEME, TitleBarTheme.SYSTEM.configValue(), TitleBarTheme.CONFIG_VALUES);
        captionColor = builder
                .comment("Title bar background as #RRGGBB. Empty uses the system color. Windows 11 only.")
                .translation(translationKey(TITLE_BAR, CAPTION_COLOR))
                .define(CAPTION_COLOR, "", ClientConfig::isColor);
        textColor = builder
                .comment("Title text as #RRGGBB. Empty uses the system color. Windows 11 only.")
                .translation(translationKey(TITLE_BAR, TEXT_COLOR))
                .define(TEXT_COLOR, "", ClientConfig::isColor);
        borderColor = builder
                .comment("Window border as #RRGGBB. Empty uses the system color. Windows 11 only.")
                .translation(translationKey(TITLE_BAR, BORDER_COLOR))
                .define(BORDER_COLOR, "", ClientConfig::isColor);
        builder.pop();

        builder.comment("Game window. Size settings take effect at the next start and are skipped in fullscreen.")
                .translation(translationKey(WINDOW))
                .push(WINDOW);
        flashWhenLoaded = builder
                .comment("Flash the taskbar button when the game has finished loading and the window is in the background.")
                .translation(translationKey(WINDOW, "flash_when_loaded"))
                .define("flash_when_loaded", true);
        flashOnDisconnect = builder
                .comment("Flash the taskbar button when a server disconnects or kicks you and the window is in the background.")
                .translation(translationKey(WINDOW, "flash_on_disconnect"))
                .define("flash_on_disconnect", false);
        initialWidth = builder
                .comment("Window width when no saved size is used. 0 keeps the default width.")
                .translation(translationKey(WINDOW, INITIAL_WIDTH))
                .gameRestart()
                .defineInRange(INITIAL_WIDTH, 0, 0, MAX_WINDOW_SIZE);
        initialHeight = builder
                .comment("Window height when no saved size is used. 0 keeps the default height.")
                .translation(translationKey(WINDOW, INITIAL_HEIGHT))
                .gameRestart()
                .defineInRange(INITIAL_HEIGHT, 0, 0, MAX_WINDOW_SIZE);
        startMaximized = builder
                .comment("Start with a maximized window when no saved size is used.")
                .translation(translationKey(WINDOW, START_MAXIMIZED))
                .gameRestart()
                .define(START_MAXIMIZED, false);
        rememberSize = builder
                .comment(
                        "Remember the window size, position and maximized state between starts.",
                        "Saved in customtitleicon/window-state.json in the game folder, next to the config and mods folders.")
                .translation(translationKey(WINDOW, REMEMBER_SIZE))
                .define(REMEMBER_SIZE, false);
        builder.pop();

        spec = builder.build();
    }

    public ModConfigSpec spec() {
        return spec;
    }

    public TitleSettings titleSettings() {
        return new TitleSettings(
                packName.get(),
                packVersion.get(),
                new TitleTemplate(menuTitle.get()),
                new TitleTemplate(singleplayerTitle.get()),
                new TitleTemplate(multiplayerTitle.get()),
                showServerAddress.get());
    }

    public String iconFile() {
        return iconFile.get();
    }

    public TitleBarSettings titleBarSettings() {
        return TitleBarSettings.parse(theme.get(), captionColor.get(), textColor.get(), borderColor.get());
    }

    public boolean flashWhenLoaded() {
        return flashWhenLoaded.get();
    }

    public boolean flashOnDisconnect() {
        return flashOnDisconnect.get();
    }

    public boolean rememberSize() {
        return rememberSize.get();
    }

    private static String translationKey(String... path) {
        return CustomTitleIcon.MOD_ID + ".configuration." + String.join(".", path);
    }

    private static boolean isColor(Object value) {
        return value instanceof String color && TitleBarColor.isValid(color);
    }
}
