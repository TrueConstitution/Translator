package kgg.translator.mixin.world;

import kgg.translator.handler.TranslateHelper;
import kgg.translator.option.WorldOption;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

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

    /**
     * @author KGG_Xing_Kong
     * @reason 翻译
     */
    @Overwrite
    public OrderedText[] getOrderedMessages(boolean filtered, Function<Text, OrderedText> messageOrderer) {
        if (autoSign != WorldOption.autoSign.isEnable() || signCombine != WorldOption.signCombine.isEnable()) {
            autoSign = WorldOption.autoSign.isEnable();
            signCombine = WorldOption.signCombine.isEnable();
            updated = true;
        }

        if (updated) {
            updated = false;
            orderedMessages = null;
        }

        if (this.orderedMessages == null || this.filtered != filtered) {
            this.filtered = filtered;
            this.orderedMessages = new OrderedText[4];
            if (autoSign) {  // 如果需要翻译
                if (signCombine) {  // 如果结合告示牌所有行翻译
                    MutableText text = this.getMessage(0, filtered).copy();
                    for (int i = 1; i < 4; ++i) {
                        text.append(this.getMessage(i, filtered));
                    }
                    Text message = TranslateHelper.translateNoWait(text, t -> {
                        updated = true;
                    });

                    List<OrderedText> list = MinecraftClient.getInstance().textRenderer.wrapLines(message, 90);  // 90来自于SignBlockEntity.getMaxTextWidth
                    // 填充为4行，多余的删掉
                    for (int i = 0; i < 4; ++i) {
                        if (i < list.size()) {
                            this.orderedMessages[i] = list.get(i);
                        } else {
                            this.orderedMessages[i] = OrderedText.empty();
                        }
                    }
                } else {
                    for (int i = 0; i < 4; ++i) {  // 否则逐行翻译
                        Text message = this.getMessage(i, filtered);
                        if (!(MinecraftClient.getInstance().currentScreen instanceof SignEditScreen)) {  // 如果没在编辑告示牌
                            message = TranslateHelper.translateNoWait(message, t -> {
                                updated = true;
                            });
                        }
                        this.orderedMessages[i] = message.asOrderedText();
                    }
                }
            } else {
                for (int i = 0; i < 4; ++i) {
                    Text message = this.getMessage(i, filtered);
                    this.orderedMessages[i] = messageOrderer.apply(message);
                }
            }
        }
        return this.orderedMessages;
    }
}
