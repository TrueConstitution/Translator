package kgg.translator.mixin.hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kgg.translator.option.ScreenOption;
import kgg.translator.handler.TranslateHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudForScoreboardMixin {
    @ModifyExpressionValue(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getDisplayName()Lnet/minecraft/text/Text;"))
    private Text modifyObjectiveName(Text original) {
        return ScreenOption.autoScoreboard.isEnable() ? TranslateHelper.translateNow(original) : original;
    }

    @ModifyExpressionValue(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private MutableText modifyPlayerName(MutableText original) {
        return ScreenOption.autoScoreboard.isEnable() ? (MutableText) TranslateHelper.translateNow(original) : original;
    }
}
