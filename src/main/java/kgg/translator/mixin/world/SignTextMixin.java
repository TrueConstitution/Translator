package kgg.translator.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kgg.translator.handler.SignHandler;
import kgg.translator.handler.TranslateHelper;
import kgg.translator.option.WorldOption;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(SignText.class)
public abstract class SignTextMixin {
    @Shadow @Nullable public OrderedText[] orderedMessages;
    @Shadow private boolean filtered;
    @Shadow public abstract Text getMessage(int line, boolean filtered);

    @Unique
    private boolean autoSign = false;
    @Unique
    private boolean signCombine = false;
    @Unique
    private boolean updated = false;

    @Inject(method = "getOrderedMessages", at = @At("HEAD"))
    private void updateFlags(boolean filtered, Function<Text, OrderedText> messageOrderer, CallbackInfoReturnable<OrderedText[]> cir) {
        if (autoSign!= WorldOption.autoSign.isEnable() || signCombine!= WorldOption.signCombine.isEnable()) {
            autoSign = WorldOption.autoSign.isEnable();
            signCombine = WorldOption.signCombine.isEnable();
            updated = true;
        }
        if (updated) {
            updated = false;
            orderedMessages = null;
        }
    }

    @Unique
    private boolean shouldTranslate() {
        return autoSign && !(MinecraftClient.getInstance().currentScreen instanceof AbstractSignEditScreen);
    }

    @ModifyExpressionValue(method = "getOrderedMessages", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignText;getMessage(IZ)Lnet/minecraft/text/Text;"))
    private Text modifySignTextIndividually(Text original) {
        if (signCombine || !shouldTranslate()) return original;
        return TranslateHelper.translateNow(original);
    }

    @Inject(
            method = "getOrderedMessages",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/block/entity/SignText;orderedMessages:[Lnet/minecraft/text/OrderedText;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER),
            cancellable = true
    )
    private void modifySignTextCombined(boolean filtered, Function<Text, OrderedText> messageOrderer, CallbackInfoReturnable<OrderedText[]> cir) {
        if (signCombine && shouldTranslate()) {
            handleCombinedTranslation();
            cir.setReturnValue(orderedMessages);
            cir.cancel();
        }
    }

    @Unique
    private void handleCombinedTranslation() {
        MutableText text = getMessage(0, filtered).copy();
        for (int i = 1; i < 4; ++i) {
            text.append(getMessage(i, filtered));
        }
        Text combinedMessage = TranslateHelper.translateNow(text, t -> updated = true);

        List<OrderedText> list = MinecraftClient.getInstance().textRenderer.wrapLines(combinedMessage, SignHandler.lineWidth);
        for (int i = 0; i < 4; ++i) {
            if (i < list.size()) {
                orderedMessages[i] = list.get(i);
            } else {
                orderedMessages[i] = OrderedText.empty();
            }
        }
    }
}