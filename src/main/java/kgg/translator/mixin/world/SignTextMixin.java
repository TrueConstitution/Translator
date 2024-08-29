package kgg.translator.mixin.world;

import kgg.translator.handler.TranslateHelper;
import kgg.translator.config.WorldOptions;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

@Mixin(SignText.class)
public abstract class SignTextMixin {
    @Shadow @Nullable public OrderedText[] orderedMessages;
    @Shadow private boolean filtered;
    @Shadow public abstract Text getMessage(int line, boolean filtered);

    @Unique
    private boolean translated = false;
    @Unique
    private boolean updated = false;

    /**
     * @author KGG_Xing_Kong
     * @reason 翻译
     */
    @Overwrite
    public OrderedText[] getOrderedMessages(boolean filtered, Function<Text, OrderedText> messageOrderer) {
        if (translated != WorldOptions.autoSign.isEnable()) {
            translated = WorldOptions.autoSign.isEnable();
            updated = true;
        }

        if (updated) {
            updated = false;
            orderedMessages = null;
        }

        if (this.orderedMessages == null || this.filtered != filtered) {
            this.filtered = filtered;
            this.orderedMessages = new OrderedText[4];
            for (int i = 0; i < 4; ++i) {
                Text message = this.getMessage(i, filtered);
                if (translated) {

                    message = TranslateHelper.translateNoWait(message, t -> {
                        updated = true;
                    });
                }

                this.orderedMessages[i] = messageOrderer.apply(message);
            }
        }
        return this.orderedMessages;
    }
}
