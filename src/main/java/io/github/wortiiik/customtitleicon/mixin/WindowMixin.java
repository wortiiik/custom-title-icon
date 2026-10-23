package io.github.wortiiik.customtitleicon.mixin;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import io.github.wortiiik.customtitleicon.WindowStartup;
import io.github.wortiiik.customtitleicon.titlebar.TitleBar;
import io.github.wortiiik.customtitleicon.window.Bounds;
import io.github.wortiiik.customtitleicon.window.GameWindow;
import net.minecraft.server.packs.PackResources;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
abstract class WindowMixin {

    @Shadow
    private int windowedX;
    @Shadow
    private int windowedY;
    @Shadow
    private int windowedWidth;
    @Shadow
    private int windowedHeight;
    @Shadow
    private boolean fullscreen;
    @Shadow
    private int x;
    @Shadow
    private int y;
    @Shadow
    private int width;
    @Shadow
    private int height;

    @Shadow
    public abstract long getWindow();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;setMode()V"))
    private void customtitleicon$placeWindow(CallbackInfo callback) {
        if (fullscreen) {
            return;
        }
        WindowStartup.placeWindow(getWindow(), new Bounds(x, y, width, height)).ifPresent(bounds -> {
            windowedX = x = bounds.x();
            windowedY = y = bounds.y();
            windowedWidth = width = bounds.width();
            windowedHeight = height = bounds.height();
        });
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;refreshFramebufferSize()V"))
    private void customtitleicon$maximizeWindow(CallbackInfo callback) {
        if (fullscreen || !GameWindow.maximizeAtStartup()) {
            return;
        }
        GLFW.glfwMaximizeWindow(getWindow());
        int[] left = new int[1];
        int[] top = new int[1];
        GLFW.glfwGetWindowPos(getWindow(), left, top);
        int[] newWidth = new int[1];
        int[] newHeight = new int[1];
        GLFW.glfwGetWindowSize(getWindow(), newWidth, newHeight);
        x = left[0];
        y = top[0];
        width = newWidth[0];
        height = newHeight[0];
    }

    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    private void customtitleicon$setIcon(PackResources packResources, IconSet iconSet, CallbackInfo callback) {
        customtitleicon$observeBounds();
        if (WindowStartup.applyAppearanceOnce(getWindow())) {
            callback.cancel();
        }
    }

    @Inject(method = "setMode", at = @At("TAIL"))
    private void customtitleicon$setMode(CallbackInfo callback) {
        TitleBar.reapply(getWindow());
    }

    @Inject(method = {"onMove", "onResize"}, at = @At("TAIL"))
    private void customtitleicon$onBoundsChanged(long window, int first, int second, CallbackInfo callback) {
        customtitleicon$observeBounds();
    }

    @Inject(method = "onFocus", at = @At("HEAD"))
    private void customtitleicon$onFocus(long window, boolean hasFocus, CallbackInfo callback) {
        if (hasFocus && window == getWindow()) {
            TitleBar.onFocusGained(window);
        }
    }

    private void customtitleicon$observeBounds() {
        if (!fullscreen) {
            GameWindow.observe(getWindow(), new Bounds(x, y, width, height));
        }
    }
}
