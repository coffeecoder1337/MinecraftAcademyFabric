package net.st1ch.minecraftacademy.education;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class EducationLevelLoader {
    private static final Map<String, EducationLevel> LEVELS = new HashMap<>();

    public static void loadLevels() {
        Identifier fileId = Identifier.of(MinecraftAcademy.MOD_ID, "education_levels.json");
        InputStream input = MinecraftServer.class.getResourceAsStream("/data/" + fileId.getNamespace() + "/" + fileId.getPath());
        if (input == null) {
            System.err.println("[Education] Не удалось загрузить файл уровней");
            return;
        }

        try (Reader reader = new InputStreamReader(input)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray levelArray = root.getAsJsonArray("levels");

            for (JsonElement el : levelArray) {
                JsonObject levelJson = el.getAsJsonObject();
                String name = levelJson.get("name").getAsString();
                List<String> layout = new ArrayList<>();
                for (JsonElement row : levelJson.getAsJsonArray("layout")) {
                    layout.add(row.getAsString());
                }
                String facing = levelJson.get("spawn_facing").getAsString();

                LEVELS.put(name, new EducationLevel(name, layout, facing));
            }

            System.out.println("[Education] Загружено уровней: " + LEVELS.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static EducationLevel getLevel(String name) {
        return LEVELS.get(name);
    }

    public static Set<String> getLevelNames() {
        return LEVELS.keySet();
    }
}
