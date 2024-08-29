package kgg.translator.mixin;

import kgg.translator.handler.chathub.ChatHubHandler;
import kgg.translator.handler.chathub.TranslateClickEvent;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public class ScreenMixinForChat {

    /**
     * 在遇到奇怪的点击操作时，查看是不是翻译操作
     */
    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 2, remap = false))
    public void error(org.slf4j.Logger instance, String s, Object o) {
        if (o instanceof TranslateClickEvent event) {
            ChatHubHandler.handleWithTranslatingTip(event.text);
        } else {
            instance.error(s, o);
        }
    }
}
