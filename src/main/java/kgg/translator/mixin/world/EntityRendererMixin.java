package kgg.translator.mixin.world;

import kgg.translator.handler.TranslateHelper;
import kgg.translator.option.TranslateOption;
import kgg.translator.option.WorldOption;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    // todo 只翻译近距离实体

    /**
     * 修改传入的text值
     */
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text getText(Text text) {
        if (!WorldOption.autoEntityName.isEnable()) return text;
        return TranslateHelper.translateNoWait(text, s -> {}, TranslateOption.disableSplitForEntityNames.isEnable());
    }
}
