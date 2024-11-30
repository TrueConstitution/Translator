package kgg.translator.exception;

import kgg.translator.translator.Translator;

public class NotConfiguredException extends TranslateException {
    private final Translator translator;
    public NotConfiguredException(Translator translator) {
        super("翻译器未配置");
        this.translator = translator;
    }

    public Translator getTranslator() {
        return translator;
    }
}
