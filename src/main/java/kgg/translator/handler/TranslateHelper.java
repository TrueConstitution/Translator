package kgg.translator.handler;

import kgg.translator.TranslatorManager;
import kgg.translator.option.TranslateOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class TranslateHelper {
    private static final Logger LOGGER = LogManager.getLogger(TranslateHelper.class);
    private static final ConcurrentHashMap<String, Long> failedTextCache = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<String> translatingTexts = new ConcurrentLinkedQueue<>();
    private static final int MAX_FAILED_TEXT_CACHE_TIME = 1000 * 60 * 2;  // 2分钟
    private static final long CHECK_TIME = 1000 * 60 * 5;  // 5分钟
    private static int minStyledSegmentSize = -1;

    public static int getMinStyledSegmentSize() {
        return minStyledSegmentSize;
    }

    public static void setMinStyledSegmentSize(int minStyledSegmentSize) {
        TranslateHelper.minStyledSegmentSize = minStyledSegmentSize;
    }

    // 清理线程
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    static {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> failedTextCache.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue() > MAX_FAILED_TEXT_CACHE_TIME), CHECK_TIME, CHECK_TIME, TimeUnit.MILLISECONDS);
    }

    public static MutableText translateNoWait(Text text) {
        return translateNoWait(text, s -> {});
    }

    public static Text getStyledText(Text text) {
        var iter = text.getSiblings().iterator();
        while ((text.getStyle().isEmpty() || text.copyContentOnly().getString().isBlank()) && iter.hasNext()) {
            text = getStyledText(iter.next());
        }
        return text;
    }

    public static MutableText translateNoWait(Text text, Consumer<String> comparable) {
        return translateNoWait(text, comparable, false);
    }

    public static MutableText translateNoWait(Text text, Consumer<String> comparable, boolean forceDisableSplit) {
        if (!TranslateOption.splitStyledTextIntoSegments.isEnable() || forceDisableSplit) {
            Style style = getStyledText(text).getStyle();
            if (style.isEmpty()) style = text.getStyle();
            return Text.literal(translateNoWait(text.getString(), comparable)).fillStyle(style);
        }
        MutableText head = Text.literal("");
        StringBuilder str = new StringBuilder();
        Style[] lastStyle = new Style[]{text.getStyle()};
        text.visit((style, asString) -> {
            str.append(asString);
            lastStyle[0] = style;
            if (str.length() >= minStyledSegmentSize) {
                head.append(Text.literal(translateNoWait(str.toString(), comparable)).setStyle(style));
                str.setLength(0);
            }
            return Optional.empty();
        }, text.getStyle());
        if (!str.isEmpty()) head.append(Text.literal(translateNoWait(str.toString(), comparable)).setStyle(lastStyle[0]));
        return head;
    }

    public static String translateNoWait(String text) {
        return translateNoWait(text, s -> {});
    }

    public static String translateNoWait(String text, Consumer<String> comparable) {
        // 检查此文本
        Long aLong = failedTextCache.get(text);
        if (aLong != null) {
            if (System.currentTimeMillis() - aLong > MAX_FAILED_TEXT_CACHE_TIME) {
                failedTextCache.remove(text);
            } else {
                return text;
            }
        }

        String cache = TranslatorManager.getFromCache(text);
        if (cache != null) {
            return cache;
        }
        if (translatingTexts.contains(text)) {
            return text;
        }

        translatingTexts.add(text);
        CompletableFuture.runAsync(() -> {
            try {
                String s = TranslatorManager.cachedTranslate(text);
                comparable.accept(s);
            } catch (Exception e) {
                failedTextCache.put(text, System.currentTimeMillis());
            } finally {
                translatingTexts.remove(text);
            }
        });
        return text;
    }

    public static void clearCache() {
        LOGGER.info("Clear failed cache");
        failedTextCache.clear();
    }
}
