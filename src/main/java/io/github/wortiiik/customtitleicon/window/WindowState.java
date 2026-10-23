package io.github.wortiiik.customtitleicon.window;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Optional;

public record WindowState(Bounds bounds, boolean maximized) {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Optional<WindowState> fromJson(String json) {
        try {
            JsonObject object = JsonParser.parseString(json).getAsJsonObject();
            Bounds bounds = new Bounds(
                    object.get("x").getAsInt(),
                    object.get("y").getAsInt(),
                    object.get("width").getAsInt(),
                    object.get("height").getAsInt());
            boolean maximized = object.has("maximized") && object.get("maximized").getAsBoolean();
            return bounds.hasArea() ? Optional.of(new WindowState(bounds, maximized)) : Optional.empty();
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    public String toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("x", bounds.x());
        object.addProperty("y", bounds.y());
        object.addProperty("width", bounds.width());
        object.addProperty("height", bounds.height());
        object.addProperty("maximized", maximized);
        return GSON.toJson(object);
    }
}
