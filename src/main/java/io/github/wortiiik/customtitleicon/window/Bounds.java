package io.github.wortiiik.customtitleicon.window;

public record Bounds(int x, int y, int width, int height) {

    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height;
    }

    public boolean hasArea() {
        return width > 0 && height > 0;
    }

    public Bounds shrunkBy(int left, int top, int right, int bottom) {
        return new Bounds(x + left, y + top, Math.max(1, width - left - right), Math.max(1, height - top - bottom));
    }

    public Bounds withSize(int newWidth, int newHeight) {
        return new Bounds(x, y, newWidth, newHeight);
    }

    public Bounds fittedInto(Bounds area) {
        int fittedWidth = Math.min(width, area.width);
        int fittedHeight = Math.min(height, area.height);
        return new Bounds(
                Math.clamp(x, area.x, area.x + area.width - fittedWidth),
                Math.clamp(y, area.y, area.y + area.height - fittedHeight),
                fittedWidth,
                fittedHeight);
    }

    public Bounds centeredIn(Bounds area) {
        int fittedWidth = Math.min(width, area.width);
        int fittedHeight = Math.min(height, area.height);
        return new Bounds(
                area.x + (area.width - fittedWidth) / 2,
                area.y + (area.height - fittedHeight) / 2,
                fittedWidth,
                fittedHeight);
    }
}
