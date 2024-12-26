package kgg.translator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import kgg.translator.exception.NoTranslatorException;
import kgg.translator.exception.TranslateException;
import kgg.translator.exception.NotConfiguredException;
import kgg.translator.ocrtrans.ResRegion;
import kgg.translator.option.TranslateOption;
import kgg.translator.translator.Translator;
import kgg.translator.util.EasyProperties;
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
    private static Translator secondary;
    private static final List<Translator> translators = new LinkedList<>();

    private static String from = "auto";
    private static String to = "zh_cn";

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
        return ocrtrans(getCurrent(), img, getFrom(), getTo());
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
    public static String translate(String text) throws TranslateException {
        return translate(text, getCurrent(), getFrom(), getTo());
    }

    public static String translate(String text, Translator translator) throws TranslateException {
        return translate(text, translator, getFrom(), getTo());
    }

    public static String translate(String text, Translator translator, String from, String to) throws TranslateException {
        if (StringUtil.isBlank(text)) return text;
        if (StringUtils.isNumeric(text)) return text;
        checkTranslator(translator);
        try {
            String translate = translator.translate(text, (String) translator.getLanguageProperties().getOrDefault(from, from), (String) translator.getLanguageProperties().getOrDefault(to, to));
            LOGGER.info("{} translate from {} to {}: \"{}\" -> \"{}\"", translator, from, to, getOutString(text), getOutString(translate));
            return translate;
        } catch (Exception e) {
            LOGGER.error("{} translate from {} to {} failed: \"{}\"", translator, from, to, getOutString(text), e);
            if (TranslateOption.useSecondaryTranslator.isEnable() && getSecondary() != null && getSecondary().isConfigured()) {
                LOGGER.info("trying secondary translator");
                return translate(text, getSecondary(), from, to);
            }
            if (e instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e);
            }
        }
    }

    public static ResRegion[] ocrtrans(Translator translator, byte[] img, String from, String to) throws TranslateException {
        checkTranslator(translator);
        LOGGER.info("{} ocrtrans, from {} to {}", translator, from, to);
        try {
            return translator.ocrtrans(img, (String) translator.getLanguageProperties().getOrDefault(from, from), (String) translator.getLanguageProperties().getOrDefault(to, to));
        } catch (Exception e) {
            LOGGER.error("{} ocrtrans, from {} to {} failed:", translator, from, to, e);
            if (TranslateOption.useSecondaryTranslator.isEnable() && getSecondary() != null && getSecondary().isConfigured()) {
                LOGGER.info("trying secondary translator");
                return ocrtrans(translator, img, from, to);
            }
            if (e instanceof TranslateException c) {
                throw c;
            } else {
                throw new TranslateException(e);
            }
        }
    }

    private static void checkTranslator(Translator translator) throws TranslateException {
        if (translator == null) {
            throw new NoTranslatorException();
        }
        if (!translator.isConfigured()) {
            throw new NotConfiguredException(translator);
        }
    }

    public static void clearCache() {
        LOGGER.info("Clear cache");
        cache.invalidateAll();
    }

    public static Translator getCurrent() {
        return current;
    }

    public static Translator getSecondary() {
        return secondary;
    }

    public static boolean setTranslator(Translator translator) {
        LOGGER.info("Set current translator to {}", translator);
        if (current != translator) {
            TranslatorManager.current = translator;
        }
        return true;
    }

    public static boolean setSecondaryTranslator(Translator translator) {
        LOGGER.info("Set secondary translator to {}", translator);
        if (secondary != translator && current != translator) {
            TranslatorManager.secondary = translator;
        }
        return true;
    }

    public static void addTranslator(Translator translator) {
        LOGGER.info("Add translator {}", translator);
        if (translators.isEmpty()) {
            setTranslator(translator);
        } else {
            if (!getCurrent().isConfigured() && translator.isConfigured()) {
                setTranslator(translator);
            }
        }
        translators.add(translator);
    }

    public static String getTo() {
        return to;
    }

    public static void setTo(String defaultTo) {
        TranslatorManager.to = defaultTo;
    }

    public static String getFrom() {
        return from;
    }

    public static void setFrom(String defaultFrom) {
        TranslatorManager.from = defaultFrom;
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
