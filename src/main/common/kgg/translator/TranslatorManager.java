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
        return ocrtrans(getCurrentTranslator(), img, getDefaultFrom(), getDefaultTo());
    }

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
    private static String translate(String text) throws TranslateException {
        return translate(text, getCurrentTranslator(), getDefaultFrom(), getDefaultTo());
    }

    private static String translate(String text, Translator translator) throws TranslateException {
        return translate(text, translator, getDefaultFrom(), getDefaultTo());
    }

    private static String translate(String text, Translator translator, String from, String to) throws TranslateException {
        if (StringUtil.isBlank(text)) return text;
        if (StringUtils.isNumeric(text)) return text;
        checkTranslator(translator);

        try {
            String translate = translator.translate(text, from, to);
            LOGGER.info("{} translate from {} to {}: \"{}\" -> \"{}\"", translator, getOutString(text), getOutString(translate), from, to);
            return translate;
        } catch (Exception e) {
            LOGGER.error("{} translate from {} to {} failed: \"{}\"", translator, from, to, getOutString(text), e);
            if (e instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e);
            }
        }
    }

    private static ResRegion[] ocrtrans(Translator translator, byte[] img, String from, String to) throws TranslateException {
        checkTranslator(translator);
        LOGGER.info("{} ocrtrans, from {} to {}", translator, from, to);
        try {
            return translator.ocrtrans(img, from, to);
        } catch (Exception e) {
            LOGGER.error("{} ocrtrans, from {} to {} failed:", translator, from, to, e);
            if (e instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e);
            }
        }
    }

    private static void checkTranslator(Translator translator) throws TranslateException {
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
        TranslatorManager.defaultTo = defaultTo;
    }

    public static String getDefaultFrom() {
        return defaultFrom;
    }

    public static void setDefaultFrom(String defaultFrom) {
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
