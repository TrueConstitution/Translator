package kgg.translator.handler.chathub;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;

public class TranslateClickEvent extends ClickEvent {
    public MutableText text;
    public TranslateClickEvent(MutableText text) {
        super(null, "");
        this.text = text;
    }
}
