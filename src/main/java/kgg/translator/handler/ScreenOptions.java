package kgg.translator.handler;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import kgg.translator.config.Option;
import kgg.translator.TranslatorManager;
import kgg.translator.exception.TranslateException;
import kgg.translator.util.TextUtil;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ScreenOptions {
    private static final Logger LOGGER = LogManager.getLogger(ScreenOptions.class);

    public static final Option autoTooltip = new Option("auto_tip", false);
    public static final Option autoTitle = new Option("auto_title", false);
    public static final Option autoScoreboard = new Option("auto_scoreboard", false);
    public static final Option autoBossBar = new Option("auto_boss_bar", false);
//
//
//
//    // 缓存翻译结果，1分钟刷新一次，翻译的结果在管理器中还有一次缓存，所以不会重复请求，只会重新请求失败的结果
//    private static final LoadingCache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(200).build(new CacheLoader<>() {
//        @Override
//        public @NotNull String load(@NotNull String key) throws Exception {
//            return translate(key);
//        }
//    });
//
//    private static String translate(String key) {
//        try {
//            return TranslatorManager.cachedTranslate(key);
//        } catch (TranslateException e) {
//            LOGGER.error("translate failed {}", e.getMessage());
//            return key;
//        }
//    }
//
//    public static Text getTranslateText(Text text) {
//        if (Option.isAutoScreenText()) {
//            if (TextUtil.getOnlyTranslateText(text) == null) {
//                String string = text.getString();
//                String c = cache.getIfPresent(string);
//                if (c != null) {
//                    return Text.literal(c).fillStyle(text.getStyle());
//                } else {
//                    CompletableFuture.runAsync(() -> {
//                        try {
//                            cache.get(string);
//                        } catch (ExecutionException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//                }
//            }
//        }
//        return text;
//    }
//
//    public static void clearCache() {
//        LOGGER.info("Clear cache");
//        cache.invalidateAll();
//    }

    public static void init() {}
}
