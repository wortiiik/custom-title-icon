package io.github.wortiiik.customtitleicon.icon;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.wortiiik.customtitleicon.config.ClientConfig;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public final class WindowIcon {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final IconSetLoader LOADER = new IconSetLoader(WindowIcon::readPng, LOGGER::warn);

    private static boolean unsupportedPlatformReported;
    private static boolean customIconShown;
    private static String loadedFile;

    private WindowIcon() {
    }

    public static void createFolder() {
        Path folder = FMLPaths.CONFIGDIR.get().resolve(ClientConfig.ICON_FOLDER);
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            LOGGER.warn("Could not create the icon folder {}", folder, e);
        }
    }

    public static boolean showAtStartup(long window, String configuredFile) {
        try {
            List<IconImage> icons = load(configuredFile);
            if (icons.isEmpty()) {
                return false;
            }
            setWindowIcon(window, icons);
            customIconShown = true;
            return true;
        } catch (RuntimeException e) {
            LOGGER.warn("Could not set the window icon at startup, using the vanilla icon", e);
            return false;
        }
    }

    public static boolean isLoadedFrom(String configuredFile) {
        return configuredFile.equals(loadedFile);
    }

    public static List<IconImage> load(String configuredFile) {
        loadedFile = configuredFile;
        if (configuredFile.isBlank() || !isPlatformSupported()) {
            return List.of();
        }
        Path file;
        try {
            file = FMLPaths.CONFIGDIR.get().resolve(configuredFile);
        } catch (InvalidPathException e) {
            LOGGER.warn("Window icon path '{}' is not valid ({}), using the vanilla icon", configuredFile, e.getReason());
            return List.of();
        }
        try {
            return LOADER.load(file);
        } catch (NoSuchFileException e) {
            if (configuredFile.equals(ClientConfig.DEFAULT_ICON_FILE)) {
                LOGGER.info("No window icon at {}, using the vanilla icon", file);
            } else {
                LOGGER.warn("Window icon {} does not exist, using the vanilla icon", file);
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Window icon {} could not be loaded ({}), using the vanilla icon", file, e.getMessage());
        }
        return List.of();
    }

    public static void apply(Minecraft minecraft, List<IconImage> icons) {
        if (!icons.isEmpty()) {
            setWindowIcon(minecraft.getWindow().getWindow(), icons);
            customIconShown = true;
        } else if (customIconShown) {
            restoreVanillaIcon(minecraft);
            customIconShown = false;
        }
    }

    private static boolean isPlatformSupported() {
        int platform = GLFW.glfwGetPlatform();
        if (platform == GLFW.GLFW_PLATFORM_WIN32 || platform == GLFW.GLFW_PLATFORM_X11) {
            return true;
        }
        if (!unsupportedPlatformReported) {
            unsupportedPlatformReported = true;
            LOGGER.info("Window icons cannot be set on this platform (macOS or Wayland), keeping the default icon");
        }
        return false;
    }

    private static IconImage readPng(Path file) throws IOException {
        try (InputStream input = Files.newInputStream(file); NativeImage image = NativeImage.read(input)) {
            int[] pixels = image.getPixelsRGBA();
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = abgrToArgb(pixels[i]);
            }
            return new IconImage(image.getWidth(), image.getHeight(), pixels);
        }
    }

    private static int abgrToArgb(int abgr) {
        return abgr & 0xFF00FF00 | (abgr & 0xFF) << 16 | (abgr >>> 16) & 0xFF;
    }

    private static void setWindowIcon(long window, List<IconImage> icons) {
        List<ByteBuffer> buffers = new ArrayList<>(icons.size());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWImage.Buffer images = GLFWImage.malloc(icons.size(), stack);
            for (int i = 0; i < icons.size(); i++) {
                IconImage icon = icons.get(i);
                ByteBuffer pixels = rgbaBytes(icon);
                buffers.add(pixels);
                images.position(i).width(icon.width()).height(icon.height()).pixels(pixels);
            }
            GLFW.glfwSetWindowIcon(window, images.position(0));
            TaskbarButton.refreshInBackground(window);
        } finally {
            buffers.forEach(MemoryUtil::memFree);
        }
    }

    private static ByteBuffer rgbaBytes(IconImage icon) {
        int[] argb = icon.pixels();
        ByteBuffer bytes = MemoryUtil.memAlloc(argb.length * 4);
        for (int pixel : argb) {
            bytes.put((byte) (pixel >> 16)).put((byte) (pixel >> 8)).put((byte) pixel).put((byte) (pixel >>> 24));
        }
        return bytes.flip();
    }

    private static void restoreVanillaIcon(Minecraft minecraft) {
        IconSet iconSet = SharedConstants.getCurrentVersion().isStable() ? IconSet.RELEASE : IconSet.SNAPSHOT;
        try {
            minecraft.getWindow().setIcon(minecraft.getVanillaPackResources(), iconSet);
            TaskbarButton.refreshInBackground(minecraft.getWindow().getWindow());
        } catch (IOException e) {
            LOGGER.warn("Could not restore the vanilla window icon", e);
        }
    }
}
