package kgg.translator.util;

import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    public static boolean isSystemText(Text text) {
        if (text.getContent() == Text.empty().getContent()) {
            for (Text sibling : text.getSiblings()) {
                if (sibling.getContent() instanceof TranslatableTextContent content) {
                    if (!Language.getInstance().hasTranslation(content.getKey())) {
                        return false;
                    }
//                return false;
//            }
                } else if (sibling.getContent() instanceof PlainTextContent content) {
                    if (!StringUtil.isBlank(content.string())) {
                        return false;
                    }
                }
            }
            return true;
//            return text.getSiblings().for.size() == 1 && text.getSiblings().get(1).getContent() instanceof TranslatableTextContent;
        } else if (text.getContent() instanceof TranslatableTextContent content) {
            if (Language.getInstance().hasTranslation(content.getKey())) {
                for (Object arg : content.getArgs()) {
                    if (arg instanceof Text t) {
                        if (!isSystemText(t)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;

//    public static boolean hasLanguage(List<TranslatableTextContent> list) {
//        if (list == null || list.isEmpty()) {
//            return false;
//        }
//        for (TranslatableTextContent content : list) {
//            if (!Language.getInstance().hasTranslation(content.getKey())) {
//                return false;
//            }
//        }
//        return true;
//    }

//    private static final List<TranslatableTextContent> no = List.of();

//    public static List<TranslatableTextContent> getOnlyTranslatableText(Text text) {
//        List<TranslatableTextContent> list = new ArrayList<>();
//        if (text.getContent() instanceof TranslatableTextContent t) {
//            list.add(t);
//        } else if (text.getContent() instanceof PlainTextContent c) {
//            if (!StringUtil.isBlank(c.string())) {
//                return null;
//            }
//        }
//        for (Text s : text.getSiblings()) {
//            List<TranslatableTextContent> c = getOnlyTranslatableText(s);
//            if (c == null) {
//                return null;
//            }
//            list.addAll(c);
//        }
//        return list;
//        if (text.getContent() == Text.empty().getContent() || text.getContent() instanceof TranslatableTextContent) {
//            List<Text> siblings = text.getSiblings();
//            siblings.forEach(text1 -> {
//                list.addAll(getOnlyTranslatableText(text1));
//            });
////            if (siblings.size() == 1) {
////                if (siblings.get(0).getContent() instanceof TranslatableTextContent content) {
////                    return content;
////                }
////            }
//        } else if (text.getContent() instanceof TranslatableTextContent content){
//            return content;
//        }
//        return list;
    }

    public static String getString(OrderedText text) {
        StringBuffer sb = new StringBuffer();
        text.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });
        return sb.toString();
    }
}
