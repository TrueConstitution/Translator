package kgg.translator.mixin.hud;

import kgg.translator.handler.ChatHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Inject(method = "init", at = @At(value = "HEAD"))
    public void init(CallbackInfo ci) {
        ChatHandler.addTip();
    }


    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void removed() {
        ChatHandler.removeTip();
        super.removed();
    }
}
