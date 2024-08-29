package kgg.translator.mixin.world;

import kgg.translator.handler.TranslateHelper;
import kgg.translator.handler.WorldOptions;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixinForEntityName {
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text getText(Text text) {
        if (!WorldOptions.autoEntityName.isEnable()) return text;
        return TranslateHelper.translateNoWait(text);
    }
}
