package io.github.wortiiik.customtitleicon.title;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

public final class WindowTitle {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static TitleSettings settings;
    private static boolean failed;

    private WindowTitle() {
    }

    public static void apply(Minecraft minecraft, TitleSettings newSettings) {
        settings = newSettings;
        failed = false;
        warnAboutUnresolved(minecraft, newSettings);
        minecraft.updateTitle();
    }

    public static Optional<String> compose(Minecraft minecraft) {
        if (settings == null || failed) {
            return Optional.empty();
        }
        try {
            String title = settings.templateFor(stateOf(minecraft)).render(variables(minecraft, settings)::resolve).strip();
            return title.isEmpty() ? Optional.empty() : Optional.of(title);
        } catch (RuntimeException e) {
            failed = true;
            LOGGER.error("Could not build the window title, using the vanilla title until the config changes", e);
            return Optional.empty();
        }
    }

    private static void warnAboutUnresolved(Minecraft minecraft, TitleSettings settings) {
        TitleVariables variables = variables(minecraft, settings);
        settings.placeholders().stream()
                .filter(placeholder -> variables.resolve(placeholder).isEmpty())
                .forEach(WindowTitle::warnAboutUnresolved);
    }

    private static void warnAboutUnresolved(Placeholder placeholder) {
        if (placeholder.name().equals(TitleVariables.MOD_VERSION) && placeholder.hasArgument()) {
            LOGGER.warn("Mod '{}' in title variable {} is not loaded, the variable is shown as written", placeholder.argument(), placeholder.text());
        } else {
            LOGGER.warn("Unknown title variable {}, it is shown as written", placeholder.text());
        }
    }

    private static GameState stateOf(Minecraft minecraft) {
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null || !connection.getConnection().isConnected()) {
            return GameState.MENU;
        }
        return minecraft.getSingleplayerServer() != null ? GameState.SINGLEPLAYER : GameState.MULTIPLAYER;
    }

    private static TitleVariables variables(Minecraft minecraft, TitleSettings settings) {
        return new TitleVariables(
                settings.packName(),
                settings.packVersion(),
                SharedConstants.getCurrentVersion().getName(),
                FMLLoader.versionInfo().neoForgeVersion(),
                ModList.get().size(),
                minecraft.getUser().getName(),
                worldName(minecraft),
                serverName(minecraft),
                settings.showServerAddress() ? serverAddress(minecraft) : "",
                WindowTitle::modVersion);
    }

    private static String worldName(Minecraft minecraft) {
        IntegratedServer server = minecraft.getSingleplayerServer();
        return server == null ? "" : server.getWorldData().getLevelName();
    }

    private static String serverName(Minecraft minecraft) {
        ServerData server = minecraft.getCurrentServer();
        return server == null ? "" : server.name;
    }

    private static String serverAddress(Minecraft minecraft) {
        ServerData server = minecraft.getCurrentServer();
        return server == null ? "" : server.ip;
    }

    private static Optional<String> modVersion(String modId) {
        return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getVersion().toString());
    }
}
