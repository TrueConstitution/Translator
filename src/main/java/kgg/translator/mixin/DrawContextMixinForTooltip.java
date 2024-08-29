package kgg.translator.mixin;

import kgg.translator.handler.TooltipHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipData;
import net.minecraft.text.Text;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public abstract class DrawContextMixinForTooltip {

    @Shadow @Deprecated public abstract void draw(Runnable drawCallback);

    @Shadow protected abstract void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner);

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"))
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci) {
        TooltipHandler.preHandle((DrawContext) (Object) this, text, x, y);
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V", at = @At("HEAD"))
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, int x, int y, CallbackInfo ci) {
        TooltipHandler.preHandle((DrawContext) (Object) this, text, x, y);
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;II)V", at = @At("RETURN"))
    public void drawTooltip(TextRenderer textRenderer, Text text, int x, int y, CallbackInfo ci) {
        TooltipHandler.preHandle((DrawContext) (Object) this, List.of(text), x, y);
    }


    @Unique
    private static TextRenderer textRenderer;
    @Unique
    private static TooltipPositioner positioner;

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"))
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        DrawContextMixinForTooltip.textRenderer = textRenderer;
        DrawContextMixinForTooltip.positioner = positioner;
    }



    @Redirect(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;getPosition(IIIIII)Lorg/joml/Vector2ic;"))
    public Vector2ic getPosition(TooltipPositioner instance, int screenWidth, int screenHeight, int x, int y, int width, int height) {
        // 原位置
        Vector2ic position = instance.getPosition(screenWidth, screenHeight, x, y, width, height);
        // 如果没有翻译文本，则直接返回原位置
        if (!TooltipHandler.drawTranslateText) {
            return position;
        } else {
            List<TooltipComponent> components = List.of(TooltipHandler.getTranslatedOrderedText());
            TooltipHandler.drawTranslateText = false;
            if (!components.isEmpty()) {
                // 计算翻译文本的矩阵大小
                int translatedRectWidth = 0;
                int translatedRectHeight = components.size() == 1 ? -2 : 0;
                for (TooltipComponent tooltipComponent : components) {
                    int k = tooltipComponent.getWidth(textRenderer);
                    if (k > translatedRectWidth) {
                        translatedRectWidth = k;
                    }
                    translatedRectHeight += tooltipComponent.getHeight();
                }
                // 返回加上翻译文本的总体尺寸
                Vector2ic newPosition = instance.getPosition(screenWidth, screenHeight, x, y, width + translatedRectWidth + 1, Math.max(translatedRectHeight, height));
                // 渲染
                drawTooltip(textRenderer, components, newPosition.x() + width + 1, y, positioner);
                return newPosition;
            } else {
                return position;
            }
        }
    }
}
