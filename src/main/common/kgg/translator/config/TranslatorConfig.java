package kgg.translator.config;

import com.google.gson.*;
import kgg.translator.TranslatorManager;
import kgg.translator.util.ConfigUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class TranslatorConfig {
    private static final File file = new File("config", "translator.json");
    private static final Logger LOGGER = LogManager.getLogger(TranslatorConfig.class);

    public static boolean readFile() {
        JsonObject config;
        try {
            config = ConfigUtil.load(file);
        } catch (IOException e) {
            LOGGER.error("Failed to read config file", e);
            return false;
        }
        return read(config);

    }

    public static boolean writeFile() {
        JsonObject config = new JsonObject();
        if (write(config)) {
            try {
                ConfigUtil.save(file, config);
                LOGGER.info("Config written successfully");
                return true;
            } catch (Exception e) {
                LOGGER.error("Failed to write config file", e);
                return false;
            }
        }
        return false;
    }

    public static boolean write(JsonObject config) {
//        config.addProperty("autoChatHud", Option.isAutoChatHud());
//        config.addProperty("addChatHudTip", Option.isAddChatHudTip());
//        config.addProperty("autoToolTip", Option.isAutoToolTip());
//        config.addProperty("autoScreenText", Option.isAutoScreenText());
        Option.getOptions().forEach(option -> {
            if (option.isEnable() != option.defaultValue) {
                config.addProperty(option.name, option.isEnable());
            }
        });

        config.addProperty("defaultFrom", TranslatorManager.getDefaultFrom());
        config.addProperty("defaultTo", TranslatorManager.getDefaultTo());

        config.addProperty("current", TranslatorManager.getCurrentTranslator().getName());
        TranslatorManager.getTranslators().forEach(translator -> {
            if (translator.isConfigured()) {
                JsonObject object = new JsonObject();
                    translator.write(object);
                config.add(translator.getName(), object);
            }
        });
        return true;
    }

    public static boolean read(JsonObject config) {
        try {
//            Option.setAutoChatHud(config.get("autoChatHud").getAsBoolean());
//            Option.setAddChatHudTip(config.get("addChatHudTip").getAsBoolean());
//            Option.setAutoToolTip(config.get("autoToolTip").getAsBoolean());
//            Option.setAutoScreenText(config.get("autoScreenText").getAsBoolean());
            Option.getOptions().forEach(option -> {
                JsonElement element = config.get(option.name);
                if (element != null) {
                    option.setEnable(element.getAsBoolean());
                }
            });
//
            TranslatorManager.setDefaultFrom(config.get("defaultFrom").getAsString());
            TranslatorManager.setDefaultTo(config.get("defaultTo").getAsString());

            String currentTranslator = config.get("current").getAsString();
            TranslatorManager.getTranslators().forEach(translator -> {
                JsonElement element = config.get(translator.getName());
                if (element != null) {
                    JsonObject translatorConfig = element.getAsJsonObject();
                    try {
                        translator.read(translatorConfig);
                    } catch (Exception e) {
                        LOGGER.error("{} failed to read config", translator.getName(), e);
                    }
                }

                if (translator.getName().equals(currentTranslator)) {
                    TranslatorManager.setCurrentTranslator(translator);
                }
            });
            LOGGER.info("Config read successfully");
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to read config", e);
            return false;
        }
    }
}
