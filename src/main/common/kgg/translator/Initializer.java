package kgg.translator;

import kgg.translator.option.ChatOption;
import kgg.translator.option.ScreenOption;
import kgg.translator.option.WorldOption;
import kgg.translator.translator.BaiduTranslatorImpl;
import kgg.translator.translator.YouDaoTranslatorImpl;

public class Initializer {
    public static void init() {
        ScreenOption.register();
        WorldOption.register();
        ChatOption.register();

        TranslatorManager.addTranslator(new YouDaoTranslatorImpl());
        TranslatorManager.addTranslator(new BaiduTranslatorImpl());
    }
}
