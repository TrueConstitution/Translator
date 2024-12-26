package kgg.translator.mixin.hud;

import kgg.translator.handler.TranslateHelper;
import kgg.translator.option.ScreenOption;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public abstract class InGameHudForHeldItem {
    @ModifyVariable(method = "renderHeldItemTooltip", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;"))
    private MutableText modifyHeldItemRenderName(MutableText original) {
        return ScreenOption.autoHeldItemName.isEnable() ? TranslateHelper.translateNoWait(original) : original;
    }
}
