package io.github.wortiiik.customtitleicon.icon;

public final class IconImage {

    private final int width;
    private final int height;
    private final int[] argb;

    public IconImage(int width, int height, int[] argb) {
        if (width < 1 || height < 1 || argb.length != width * height) {
            throw new IllegalArgumentException("Expected " + width + "x" + height + " pixels, got " + argb.length);
        }
        this.width = width;
        this.height = height;
        this.argb = argb.clone();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean isSquare() {
        return width == height;
    }

    public int pixel(int x, int y) {
        return argb[y * width + x];
    }

    public int[] pixels() {
        return argb.clone();
    }
}
