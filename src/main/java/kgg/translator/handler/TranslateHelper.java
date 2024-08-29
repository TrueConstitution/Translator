package kgg.translator.handler;

import kgg.translator.TranslatorManager;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class TranslateHelper {
    private static final Logger LOGGER = LogManager.getLogger(TranslateHelper.class);
    // 4个线程

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final ConcurrentHashMap<String, Long> FAILED_TEXT_CACHE = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_TEXT_CACHE_TIME = 1000 * 60 * 2;  // 2分钟
    private static final long CHECK_TIME = 1000 * 60 * 5;  // 5分钟
    // 清理线程
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    static {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            FAILED_TEXT_CACHE.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue() > MAX_FAILED_TEXT_CACHE_TIME);
        }, CHECK_TIME, CHECK_TIME, TimeUnit.MILLISECONDS);
    }

    public static Text translateNoWait(Text text) {
        return translateNoWait(text, s -> {});
    }

    public static Text translateNoWait(Text text, Consumer<String> comparable) {
        return Text.literal(translateNoWait(text.getString(), comparable)).fillStyle(text.getStyle());
    }

    public static String translateNoWait(String text) {
        return translateNoWait(text, s -> {});
    }

    public static String translateNoWait(String text, Consumer<String> comparable) {
        // 检查此文本
        Long aLong = FAILED_TEXT_CACHE.get(text);
        if (aLong != null) {
            if (System.currentTimeMillis() - aLong > MAX_FAILED_TEXT_CACHE_TIME) {
                FAILED_TEXT_CACHE.remove(text);
            } else {
                return text;
            }
        }

        String cache = TranslatorManager.getFromCache(text);
        if (cache != null) {
            return cache;
        } else {
            EXECUTOR.execute(() -> {
                try {
                    String s = TranslatorManager.cachedTranslate(text);
                    comparable.accept(s);
                } catch (Exception e) {
                    FAILED_TEXT_CACHE.put(text, System.currentTimeMillis());
                }
            });
            return text;
        }
    }

    public static void clearCache() {
        LOGGER.info("Clear failed cache");
        FAILED_TEXT_CACHE.clear();
    }
}
