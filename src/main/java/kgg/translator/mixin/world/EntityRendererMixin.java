package kgg.translator.mixin.world;

import com.llamalad7.mixinextras.sugar.Local;
import kgg.translator.handler.TranslateHelper;
import kgg.translator.option.TranslateOption;
import kgg.translator.option.WorldOption;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    /**
     * 修改传入的text值
     */
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text getText(Text text, @Local(argsOnly = true) T entity) {
        if (!WorldOption.autoEntityName.isEnable() || WorldOption.autoEntityName_maxDist < dispatcher.getSquaredDistanceToCamera(entity)) return text;
        return TranslateHelper.translateText(text, s -> {}, TranslateOption.disableSplitForEntityNames.isEnable(), false);
    }
}