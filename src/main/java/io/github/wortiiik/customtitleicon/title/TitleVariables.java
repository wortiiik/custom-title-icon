package io.github.wortiiik.customtitleicon.title;

import java.util.Optional;
import java.util.function.Function;

public record TitleVariables(
        String packName,
        String packVersion,
        String minecraftVersion,
        String neoForgeVersion,
        int modCount,
        String username,
        String world,
        String server,
        String address,
        Function<String, Optional<String>> modVersions) {

    public static final String MOD_VERSION = "modversion";

    public Optional<String> resolve(Placeholder placeholder) {
        if (placeholder.name().equals(MOD_VERSION)) {
            return placeholder.hasArgument() ? modVersions.apply(placeholder.argument()) : Optional.empty();
        }
        if (placeholder.hasArgument()) {
            return Optional.empty();
        }
        return Optional.ofNullable(switch (placeholder.name()) {
            case "packname" -> packName;
            case "packversion" -> packVersion;
            case "mcversion" -> minecraftVersion;
            case "neoforge" -> neoForgeVersion;
            case "modcount" -> Integer.toString(modCount);
            case "username" -> username;
            case "world" -> world;
            case "server" -> server;
            case "address" -> address;
            default -> null;
        });
    }
}
