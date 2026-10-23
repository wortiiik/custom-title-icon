package io.github.wortiiik.customtitleicon.icon;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class IconSizes {

    public static final List<Integer> STANDARD = List.of(16, 24, 32, 48, 64, 128, 256);

    private static final int LARGEST = STANDARD.getLast();

    private IconSizes() {
    }

    public static SortedSet<Integer> generatedFrom(int sourceSize) {
        SortedSet<Integer> sizes = new TreeSet<>();
        STANDARD.stream().filter(size -> size <= sourceSize).forEach(sizes::add);
        if (sourceSize < LARGEST) {
            sizes.add(sourceSize);
        }
        return sizes;
    }
}
