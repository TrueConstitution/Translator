package kgg.translator.exception;

import kgg.translator.translator.Translator;

public class TranslatorNotConfiguredException extends TranslateException {
    private final Translator translator;
    public TranslatorNotConfiguredException(Translator translator) {
        super("翻译器未配置");
        this.translator = translator;
    }

    public Translator getTranslator() {
        return translator;
    }
}
