package kgg.translator.mixin.hud;

import kgg.translator.option.ScreenOption;
import kgg.translator.handler.TranslateHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;"))
    public Text render(ClientBossBar instance) {
        if (!ScreenOption.autoBossBar.isEnable()) return instance.getName();
        return TranslateHelper.translateNoWait(instance.getName());
    }
}
