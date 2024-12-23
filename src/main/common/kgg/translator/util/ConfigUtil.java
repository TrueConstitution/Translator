package kgg.translator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static JsonObject load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] contentBytes = fis.readAllBytes();
            String content = new String(contentBytes, StandardCharsets.UTF_8);
            return (JsonObject) JsonParser.parseString(content);
        }
    }

    public static void save(File file, JsonObject object) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(object));
        }
    }
}
