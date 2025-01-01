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
    private static final ConcurrentHashMap<String, CompletableFuture<String>> ongoingTranslations = new ConcurrentHashMap<>();
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

    public static Text getStyledText(Text text) {
        var iter = text.getSiblings().iterator();
        while ((text.getStyle().isEmpty() || text.copyContentOnly().getString().isBlank()) && iter.hasNext()) {
            text = getStyledText(iter.next());
        }
        return text;
    }
    
    public static Text translateNow(Text text) {
        return translateNow(text, s -> {});
    }

    public static Text translateNow(Text text, Consumer<String> comparable) {
        return translateText(text, comparable, false, false);
    }

    public static Text translateText(Text text, Consumer<String> comparable, boolean forceDisableSplit, boolean wait) {
        if (!TranslateOption.splitStyledTextIntoSegments.isEnable() || forceDisableSplit) {
            Style style = getStyledText(text).getStyle();
            Style finalStyle = style.isEmpty() ? text.getStyle() : style;
            var fut = translateAsync(text.getString(), comparable);
            return Text.literal(wait ? fut.join() : fut.getNow(text.getString())).fillStyle(finalStyle);
        }
        MutableText head = Text.literal("");
        StringBuilder str = new StringBuilder();
        Style[] lastStyle = new Style[]{text.getStyle()};
        text.visit((style, asString) -> {
            if (asString.length() < minStyledSegmentSize) {
                str.append(asString);
            } else {
                if (!str.isEmpty()) {
                    var fut = translateAsync(str.toString(), comparable);
                    head.append(Text.literal(wait ? fut.join() : fut.getNow(str.toString())).setStyle(lastStyle[0]));
                    str.setLength(0);
                }
                var fut = translateAsync(asString, comparable);
                head.append(Text.literal(wait ? fut.join() : fut.getNow(asString)).setStyle(style));
            }
            lastStyle[0] = style;
            return Optional.empty();
        }, text.getStyle());
        if (!str.isEmpty()) {
            var fut = translateAsync(str.toString(), comparable);
            head.append(Text.literal(wait ? fut.join() : fut.getNow(str.toString())).setStyle(lastStyle[0]));
        }
        return head.getSiblings().size() == 1 ? head.getSiblings().get(0) : head;
    }

    public static CompletableFuture<String> translateAsync(String text, Consumer<String> comparable) {
        Long aLong = failedTextCache.get(text);
        if (aLong != null) {
            if (System.currentTimeMillis() - aLong > MAX_FAILED_TEXT_CACHE_TIME) {
                failedTextCache.remove(text);
            } else {
                return CompletableFuture.completedFuture(text);
            }
        }

        String cache = TranslatorManager.getFromCache(text);
        if (cache != null) {
            return CompletableFuture.completedFuture(cache);
        }

        return ongoingTranslations.computeIfAbsent(text, k -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            CompletableFuture.runAsync(() -> {
                try {
                    String result = TranslatorManager.cachedTranslate(text);
                    comparable.accept(result);
                    future.complete(result);
                } catch (Exception e) {
                    failedTextCache.put(text, System.currentTimeMillis());
                    future.complete(text);
                } finally {
                    ongoingTranslations.remove(text);
                }
            });
            return future;
        });
    }

    public static void clearCache() {
        LOGGER.info("Clear failed cache");
        failedTextCache.clear();
    }
}
