package kgg.translator;

import kgg.translator.handler.ScreenTextHandler;
import kgg.translator.handler.WorldTextHandler;

public class CacheManager {
    public static void clearCache() {
        TranslatorManager.clearCache();
        ScreenTextHandler.clearCache();
        WorldTextHandler.clearCache();
    }
}
