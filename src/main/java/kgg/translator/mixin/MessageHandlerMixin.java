package kgg.translator.mixin;

import kgg.translator.handler.ChatHandler;
import kgg.translator.option.ChatOption;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
    @Inject(method = "onGameMessage", at = @At("TAIL"))
    private void onChatMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (ChatOption.translateModMessages.isEnable()) ChatHandler.handleNewMessage(message);
    }
}
