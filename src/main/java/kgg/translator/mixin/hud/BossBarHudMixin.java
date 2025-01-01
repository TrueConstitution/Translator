package kgg.translator.mixin.hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kgg.translator.option.ScreenOption;
import kgg.translator.handler.TranslateHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;"))
    public Text render(Text original) {
        return ScreenOption.autoBossBar.isEnable() ? TranslateHelper.translateNow(original) : original;
    }
}
