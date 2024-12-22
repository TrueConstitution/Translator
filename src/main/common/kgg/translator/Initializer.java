package kgg.translator;

import kgg.translator.option.ChatOption;
import kgg.translator.option.ScreenOption;
import kgg.translator.option.WorldOption;

public class Initializer {
    public static void init() {
        ScreenOption.register();
        WorldOption.register();
        ChatOption.register();
    }
}
