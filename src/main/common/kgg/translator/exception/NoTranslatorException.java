package kgg.translator.exception;

public class NoTranslatorException extends TranslateException {
    public NoTranslatorException() {
        super("未选择翻译器");
    }
}
