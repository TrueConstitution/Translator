package kgg.translator.mixin.hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kgg.translator.handler.TranslateHelper;
import kgg.translator.option.ScreenOption;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudForHeldItem {
    @ModifyExpressionValue(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 0))
    private MutableText modifyHeldItemRenderName(MutableText original) {
        return ScreenOption.autoHeldItemName.isEnable() ? (MutableText) TranslateHelper.translateNow(original) : original;
    }
}
