package kgg.translator.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TextUtil {
    @Nullable
    public static TranslatableTextContent getOnlyTranslateText(Text text) {
        if (text.getContent() == Text.empty().getContent()) {
            List<Text> siblings = text.getSiblings();
            if (siblings.size() == 1) {
                if (siblings.get(0).getContent() instanceof TranslatableTextContent content) {
                    return content;
                }
            }
        } else if (text.getContent() instanceof TranslatableTextContent content){
            return content;
        }
        return null;
    }
}
