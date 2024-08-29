package kgg.translator.mixin.world;

import kgg.translator.handler.TranslateHelper;
import kgg.translator.config.WorldOptions;
import net.minecraft.entity.decoration.DisplayEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DisplayEntity.TextDisplayEntity.class)
public class TextDisplayEntityMixin {
    @Shadow @Nullable private DisplayEntity.TextDisplayEntity.@Nullable TextLines textLines;
    @Unique
    private boolean translated = false;
    @Unique
    private boolean updated = false;

    @ModifyVariable(method = "splitLines", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public DisplayEntity.TextDisplayEntity.LineSplitter getLineSplitter(DisplayEntity.TextDisplayEntity.LineSplitter splitter) {
        if (translated != WorldOptions.autoEntityName.isEnable()) {
            translated = WorldOptions.autoEntityName.isEnable();
            updated = true;
        }

        if (updated) {
            updated = false;
            textLines = null;
        }

        return (text, w) -> {
            if (translated){
                text = TranslateHelper.translateNoWait(text, t -> {
                    updated = true;
                });
            }
            return splitter.split(text, w);
        };
    }
}
