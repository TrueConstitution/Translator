package kgg.translator.handler;

import kgg.translator.config.Option;

public class WorldOptions {
    public static final Option autoEntityName = new Option("auto_entity_name", false);
    public static final Option autoSign = new Option("auto_sign", false);
    public static void init() {}
//    private static final Logger LOGGER = LogManager.getLogger(WorldOptions.class);
//
//
//    // 缓存翻译结果，1分钟刷新一次，翻译的结果在管理器中还有一次缓存，所以不会重复请求，只会重新请求失败的结果
//    private static final LoadingCache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(600).build(new CacheLoader<>() {
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
//        return getTranslateText(text, t -> {});
//    }
//
//    public static Text getTranslateText(Text text, Consumer<String> comparable) {

//        if (Option.isAutoWorldText()) {
//            if (TextUtil.getOnlyTranslateText(text) == null) {
//                String string = text.getString();
//                String c = cache.getIfPresent(string);
//                if (c != null) {
//                    return Text.literal(c).fillStyle(text.getStyle());
//                } else {
//                    CompletableFuture.supplyAsync(() -> {
//                        try {
//                            return cache.get(string);
//                        } catch (ExecutionException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }).thenAccept(comparable);
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
}
