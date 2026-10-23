package io.github.wortiiik.customtitleicon.icon;

public final class IconResampler {

    private static final int CHANNELS = 4;

    private IconResampler() {
    }

    public static IconImage downscale(IconImage source, int size) {
        if (!source.isSquare()) {
            throw new IllegalArgumentException("Source must be square, got " + source.width() + "x" + source.height());
        }
        if (size < 1 || size > source.width()) {
            throw new IllegalArgumentException("Cannot downscale " + source.width() + " pixels to " + size);
        }
        if (size == source.width()) {
            return source;
        }
        Span[] spans = spans(source.width(), size);
        float[] columns = resizeRowsAndTranspose(premultiply(source), source.width(), source.height(), spans);
        float[] result = resizeRowsAndTranspose(columns, source.height(), size, spans);
        return unpremultiply(result, size);
    }

    private static Span[] spans(int sourceLength, int targetLength) {
        Span[] spans = new Span[targetLength];
        for (int target = 0; target < targetLength; target++) {
            long start = (long) target * sourceLength;
            long end = start + sourceLength;
            int first = (int) (start / targetLength);
            int last = (int) ((end - 1) / targetLength);
            float[] weights = new float[last - first + 1];
            for (int source = first; source <= last; source++) {
                long overlap = Math.min(end, (long) (source + 1) * targetLength) - Math.max(start, (long) source * targetLength);
                weights[source - first] = (float) overlap / sourceLength;
            }
            spans[target] = new Span(first, weights);
        }
        return spans;
    }

    private static float[] resizeRowsAndTranspose(float[] pixels, int width, int height, Span[] spans) {
        int targetWidth = spans.length;
        float[] result = new float[targetWidth * height * CHANNELS];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < targetWidth; x++) {
                Span span = spans[x];
                int target = (x * height + y) * CHANNELS;
                for (int i = 0; i < span.weights().length; i++) {
                    int source = (y * width + span.first() + i) * CHANNELS;
                    float weight = span.weights()[i];
                    for (int channel = 0; channel < CHANNELS; channel++) {
                        result[target + channel] += pixels[source + channel] * weight;
                    }
                }
            }
        }
        return result;
    }

    private static float[] premultiply(IconImage image) {
        int[] argb = image.pixels();
        float[] result = new float[argb.length * CHANNELS];
        for (int i = 0; i < argb.length; i++) {
            int pixel = argb[i];
            float alpha = pixel >>> 24;
            float coverage = alpha / 255f;
            result[i * CHANNELS] = ((pixel >> 16) & 0xFF) * coverage;
            result[i * CHANNELS + 1] = ((pixel >> 8) & 0xFF) * coverage;
            result[i * CHANNELS + 2] = (pixel & 0xFF) * coverage;
            result[i * CHANNELS + 3] = alpha;
        }
        return result;
    }

    private static IconImage unpremultiply(float[] pixels, int size) {
        int[] argb = new int[size * size];
        for (int i = 0; i < argb.length; i++) {
            float alpha = pixels[i * CHANNELS + 3];
            int a = toByte(alpha);
            if (a == 0) {
                continue;
            }
            float scale = 255f / alpha;
            int r = toByte(pixels[i * CHANNELS] * scale);
            int g = toByte(pixels[i * CHANNELS + 1] * scale);
            int b = toByte(pixels[i * CHANNELS + 2] * scale);
            argb[i] = a << 24 | r << 16 | g << 8 | b;
        }
        return new IconImage(size, size, argb);
    }

    private static int toByte(float value) {
        return Math.clamp(Math.round(value), 0, 255);
    }

    private record Span(int first, float[] weights) {
    }
}
