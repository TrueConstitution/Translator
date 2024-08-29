package kgg.translator;

import kgg.translator.config.ChatOptions;
import kgg.translator.config.ScreenOptions;
import kgg.translator.config.WorldOptions;
import kgg.translator.translator.BaiduTranslatorImpl;
import kgg.translator.translator.YouDaoTranslatorImpl;

public class Initializer {
    public static void init() {
        ScreenOptions.register();
        WorldOptions.register();
        ChatOptions.register();

        TranslatorManager.addTranslator(new YouDaoTranslatorImpl());
        TranslatorManager.addTranslator(new BaiduTranslatorImpl());
    }
}
