package kgg.translator;

import com.google.gson.*;
import kgg.translator.option.Option;
import kgg.translator.util.ConfigUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class TranslatorConfig {
    private static final File file = new File("config", "translator.json");
    private static final File optionFile = new File("config", "translator_option.json");
    private static final Logger LOGGER = LogManager.getLogger(TranslatorConfig.class);


    public static boolean readFile() {
        JsonObject config;
        try {
            config = ConfigUtil.load(file);
            JsonObject options = ConfigUtil.load(optionFile);
            boolean b = readOptions(options);
            assert b;
        } catch (Exception e) {
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
                JsonObject options = new JsonObject();
                boolean b = writeOptions(options);
                assert b;
                ConfigUtil.save(optionFile, options);
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
        config.addProperty("from", TranslatorManager.getFrom());
        config.addProperty("to", TranslatorManager.getTo());

        config.addProperty("current", TranslatorManager.getCurrent().getName());
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
            TranslatorManager.setFrom(config.get("from").getAsString());
            TranslatorManager.setTo(config.get("to").getAsString());

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

    private static boolean readOptions(JsonObject config) {
        try {
            for (Option option : Option.getOptions()) {
                JsonElement element = config.get(option.name);
                if (element != null) {
                    option.setEnable(element.getAsBoolean());
                }
            }
            LOGGER.info("Options read successfully");
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to read options", e);
            return false;
        }
    }

    private static boolean writeOptions(JsonObject config) {
        for (Option option : Option.getOptions()) {
            config.addProperty(option.name, option.isEnable());
        }
        return true;
    }
}
