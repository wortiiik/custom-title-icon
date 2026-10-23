package io.github.wortiiik.customtitleicon;

import io.github.wortiiik.customtitleicon.config.ClientConfig;
import io.github.wortiiik.customtitleicon.icon.IconImage;
import io.github.wortiiik.customtitleicon.icon.WindowIcon;
import io.github.wortiiik.customtitleicon.title.TitleSettings;
import io.github.wortiiik.customtitleicon.title.WindowTitle;
import io.github.wortiiik.customtitleicon.titlebar.TitleBar;
import io.github.wortiiik.customtitleicon.titlebar.TitleBarSettings;
import io.github.wortiiik.customtitleicon.window.GameWindow;
import io.github.wortiiik.customtitleicon.window.WindowAttention;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

@Mod(value = CustomTitleIcon.MOD_ID, dist = Dist.CLIENT)
public final class CustomTitleIcon {

    public static final String MOD_ID = "customtitleicon";

    private final ClientConfig config = new ClientConfig();

    public CustomTitleIcon(IEventBus modBus, ModContainer container) {
        WindowIcon.createFolder();
        container.registerConfig(ModConfig.Type.CLIENT, config.spec(), ClientConfig.FILE_NAME);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(ModConfigEvent.Loading.class, event -> applyConfig(false));
        modBus.addListener(ModConfigEvent.Reloading.class, event -> applyConfig(true));
        NeoForge.EVENT_BUS.addListener(ScreenEvent.Opening.class, new WindowAttention(config)::onScreenOpening);
        NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, event -> saveWindowState());
    }

    private void saveWindowState() {
        if (config.rememberSize()) {
            GameWindow.saveState();
        }
    }

    private void applyConfig(boolean changed) {
        TitleSettings titleSettings = config.titleSettings();
        TitleBarSettings titleBarSettings = config.titleBarSettings();
        String iconFile = config.iconFile();
        boolean reloadIcon = changed || !WindowIcon.isLoadedFrom(iconFile);
        List<IconImage> icons = reloadIcon ? WindowIcon.load(iconFile) : List.of();
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            WindowTitle.apply(minecraft, titleSettings);
            TitleBar.apply(minecraft.getWindow().getWindow(), titleBarSettings);
            if (reloadIcon) {
                WindowIcon.apply(minecraft, icons);
            }
        });
    }
}
