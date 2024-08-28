package kgg.translator.mixin.world;

import kgg.translator.Options;
import kgg.translator.handler.WorldTextHandler;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRendererMixin {
    @Shadow @Final private TextRenderer textRenderer;

    @Unique
    int lineWidth;

    @Inject(method = "renderText", at = @At(value = "HEAD"))
    public void renderText(BlockPos pos, SignText signText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int lineHeight, int lineWidth, boolean front, CallbackInfo ci) {
        this.lineWidth = lineWidth;
    }

    @Unique
    private static boolean translated = false;

    @Redirect(method = "renderText", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignText;getOrderedMessages(ZLjava/util/function/Function;)[Lnet/minecraft/text/OrderedText;"))
    public OrderedText[] getOrderedMessages(SignText instance, boolean filtered, Function<Text, OrderedText> messageOrderer) {
        if (translated != Options.isAutoWorldText()) {
            translated = Options.isAutoWorldText();
            instance.orderedMessages = null;
        }

        return instance.getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), text -> {
            text = WorldTextHandler.getTranslateText(text);

            List<OrderedText> list = this.textRenderer.wrapLines(text, lineWidth);
            return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
        });
    }
}
