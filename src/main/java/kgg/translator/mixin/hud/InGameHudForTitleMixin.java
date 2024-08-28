package kgg.translator.mixin.hud;

import kgg.translator.handler.ScreenTextHandler;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudForTitleMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"), index = 0)
    public StringVisitable getWidth(StringVisitable text) {
        return ScreenTextHandler.getTranslateText((Text) text);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"), index = 1)
    public Text render(Text text) {
        return ScreenTextHandler.getTranslateText(text);
    }
}
