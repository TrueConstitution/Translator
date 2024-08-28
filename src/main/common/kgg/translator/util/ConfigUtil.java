package kgg.translator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public class ConfigUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonObject load(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return (JsonObject) JsonParser.parseReader(reader);
        }
    }

    public static void save(File file, JsonObject object) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(object, new JsonWriter(writer));
        }
    }
}
