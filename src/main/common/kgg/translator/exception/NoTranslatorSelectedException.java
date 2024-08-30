package kgg.translator.exception;

public class NoTranslatorSelectedException extends TranslateException {
    public NoTranslatorSelectedException() {
        super("未选择翻译器");
    }
}
