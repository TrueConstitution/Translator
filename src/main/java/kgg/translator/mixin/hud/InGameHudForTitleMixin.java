package kgg.translator.mixin.hud;

import kgg.translator.option.ScreenOption;
import kgg.translator.handler.TranslateHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudForTitleMixin {
    @ModifyArg(method = "renderTitleAndSubtitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"), index = 0)
    public StringVisitable getWidth(StringVisitable text) {
        if (!ScreenOption.autoTitle.isEnable()) return text;
        return TranslateHelper.translateNoWait((Text) text);
    }

    @ModifyArg(method = "renderTitleAndSubtitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"), index = 1)
    public Text render(Text text) {
        if (!ScreenOption.autoTitle.isEnable()) return text;
        return TranslateHelper.translateNoWait(text);
    }

    @ModifyArg(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"), index = 0)
    public StringVisitable getWidth1(StringVisitable text) {
        if (!ScreenOption.autoTitle.isEnable()) return text;
        return TranslateHelper.translateNoWait((Text) text);
    }

    @ModifyArg(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"), index = 1)
    public Text render1(Text text) {
        if (!ScreenOption.autoTitle.isEnable()) return text;
        return TranslateHelper.translateNoWait(text);
    }
}
