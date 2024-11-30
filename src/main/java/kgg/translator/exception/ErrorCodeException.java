package kgg.translator.exception;

import net.minecraft.text.Text;

public class ErrorCodeException extends TranslateException {
    public final String code;
    public final String id;

    public ErrorCodeException(String id, String code) {
        this.id = id;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return Text.translatable("translator.error.%s.%s".formatted(id, code)).getString();
    }
}
