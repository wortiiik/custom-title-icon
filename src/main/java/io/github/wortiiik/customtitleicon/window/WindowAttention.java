package io.github.wortiiik.customtitleicon.window;

import io.github.wortiiik.customtitleicon.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

public final class WindowAttention {

    private final ClientConfig config;
    private boolean loadingFinished;

    public WindowAttention(ClientConfig config) {
        this.config = config;
    }

    public void onScreenOpening(ScreenEvent.Opening event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!loadingFinished && minecraft.isGameLoadFinished()) {
            loadingFinished = true;
            if (config.flashWhenLoaded()) {
                requestIfInactive(minecraft);
            }
        }
        if (event.getNewScreen() instanceof DisconnectedScreen && config.flashOnDisconnect()) {
            requestIfInactive(minecraft);
        }
    }

    private static void requestIfInactive(Minecraft minecraft) {
        long window = minecraft.getWindow().getWindow();
        if (!WindowFocus.isInForeground(window)) {
            GLFW.glfwRequestWindowAttention(window);
        }
    }
}
