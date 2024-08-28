package kgg.translator.mixin.world;

import kgg.translator.handler.WorldTextHandler;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DisplayEntityRenderer.TextDisplayEntityRenderer.class)
public class TextDisplayEntityRendererMixin {
    @ModifyVariable(method = "getLines", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public Text getText(Text text) {
        return WorldTextHandler.getTranslateText(text);
    }
}
