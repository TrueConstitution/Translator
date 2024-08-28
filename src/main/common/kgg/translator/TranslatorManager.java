package kgg.translator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import kgg.translator.exception.NoTranslatorSelectedException;
import kgg.translator.exception.TranslateException;
import kgg.translator.exception.TranslatorNotConfiguredException;
import kgg.translator.ocr.ResRegion;
import kgg.translator.translator.Translator;
import kgg.translator.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TranslatorManager {
    private static final Logger LOGGER = LogManager.getLogger(TranslatorManager.class);
    private static Translator current;
    private static final List<Translator> translators = new LinkedList<>();

    private static String defaultFrom = "auto";
    private static String defaultTo = "zh-cn";

    private static final LoadingCache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<>() {
        @Override
        public @NotNull String load(@NotNull String key) throws Exception {
            return translate(key);
        }
    });

    public static String getFromCache(String text) {
        return cache.getIfPresent(text);
    }

    public static ResRegion[] ocrtrans(byte[] img) throws TranslateException {
        return ocrtrans(img, getDefaultFrom(), getDefaultTo());
    }

    // TODO: 2024/8/28 使用固定线程池

    public static String cachedTranslate(String text) throws TranslateException {
        try {
            return cache.get(text);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e.getCause());
            }
        }
    }
    public static String translate(String text) throws TranslateException {
        return translate(text, getCurrentTranslator());
    }

    // TODO: 2024/8/28 getDefaultFrom赋值
    public static String translate(String text, Translator translator) throws TranslateException {
//        LOGGER.info("Use {} translate {}, from {} to {}", translator, text, getDefaultFrom(), getDefaultTo());
        if (StringUtil.isBlank(text)) return text;
        if (StringUtils.isNumeric(text)) return text;

        checkCurrentTranslator();
        String defaultTo1 = getDefaultTo();
        String defaultFrom1 = getDefaultFrom();
        try {
            String translate = translator.translate(text, defaultFrom1, defaultTo1);
            LOGGER.info("Translate: \"{}\" -> \"{}\"", getOutString(text), getOutString(translate));
            return translate;
        } catch (Exception e) {
            LOGGER.error("Translate \"{}\" failed:", getOutString(text), e);
            if (e instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e);
            }
        }
    }

    // TODO: 2024/8/28 详细的日志
    public static ResRegion[] ocrtrans(byte[] img, String from, String to) throws TranslateException {
        checkCurrentTranslator();
        Translator translator = getCurrentTranslator();
        LOGGER.info("Use {} ocrtrans, from {} to {}", translator, from, to);
        try {
            return translator.ocrtrans(img, from, to);
        } catch (Exception e) {
            LOGGER.error("OCR failed:", e);
            if (e instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e);
            }
        }
    }

    private static void checkCurrentTranslator() throws TranslateException {
        Translator translator = getCurrentTranslator();
        if (translator == null) {
            throw new NoTranslatorSelectedException();
        }
        if (!translator.isConfigured()) {
            throw new TranslatorNotConfiguredException(translator);
        }
    }

    public static void clearCache() {
        LOGGER.info("Clear cache");
        cache.invalidateAll();
    }

    public static Translator getCurrentTranslator() {
        return current;
    }

    public static boolean setCurrentTranslator(Translator translator) {
        LOGGER.info("Set current translator to {}", translator);
        boolean b = current != null && !current.equals(translator);
        if (b) {
            CacheManager.clearCache();
        }
        TranslatorManager.current = translator;
        return b;
    }

    public static void addTranslator(Translator translator) {
        LOGGER.info("Add translator {}", translator);
        if (translators.size() == 0) {
            setCurrentTranslator(translator);
        } else {
            if (!getCurrentTranslator().isConfigured() && translator.isConfigured()) {
                setCurrentTranslator(translator);
            }
        }
        translators.add(translator);
    }

    public static String getDefaultTo() {
        return defaultTo;
    }

    public static void setDefaultTo(String defaultTo) {
        if (!TranslatorManager.defaultTo.equals(defaultTo)) {
            CacheManager.clearCache();
        }
        TranslatorManager.defaultTo = defaultTo;
    }

    public static String getDefaultFrom() {
        return defaultFrom;
    }

    public static void setDefaultFrom(String defaultFrom) {
        if (!TranslatorManager.defaultFrom.equals(defaultFrom)) {
            CacheManager.clearCache();
        }
        TranslatorManager.defaultFrom = defaultFrom;
    }

    public static List<Translator> getTranslators() {
        return translators;
    }

    private static String getOutString(String text) {
        if (text.length() > 20) {
            return text.substring(0, 20) + "...";
        }
        return text;
    }
}
