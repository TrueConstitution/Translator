package kgg.translator.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class LanguageUtil {
    public enum Language {
        ZH_CN(
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        ),
        ZH_TW(
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
        ),
        JA(
                Character.UnicodeBlock.HIRAGANA,
                Character.UnicodeBlock.KATAKANA,
                Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
        ),
        KO(
                Character.UnicodeBlock.HANGUL_SYLLABLES,
                Character.UnicodeBlock.HANGUL_JAMO,
                Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
        ),
        FR(
                Character.UnicodeBlock.BASIC_LATIN,
                Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
                Character.UnicodeBlock.LATIN_EXTENDED_A,
                Character.UnicodeBlock.LATIN_EXTENDED_B
        ),
        DE(
                Character.UnicodeBlock.BASIC_LATIN,
                Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
                Character.UnicodeBlock.LATIN_EXTENDED_A,
                Character.UnicodeBlock.LATIN_EXTENDED_B
        ),
        PT(
                Character.UnicodeBlock.BASIC_LATIN,
                Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
                Character.UnicodeBlock.LATIN_EXTENDED_A,
                Character.UnicodeBlock.LATIN_EXTENDED_B
        ),
        RU(
                Character.UnicodeBlock.CYRILLIC,
                Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY,
                Character.UnicodeBlock.CYRILLIC_EXTENDED_A,
                Character.UnicodeBlock.CYRILLIC_EXTENDED_B
        ),
        EN(
                Character.UnicodeBlock.BASIC_LATIN,
                Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
                Character.UnicodeBlock.LATIN_EXTENDED_A,
                Character.UnicodeBlock.LATIN_EXTENDED_B
        ),
        UNSUPPORTED();

        private final Set<Character.UnicodeBlock> unicodeBlocks;

        Language(Character.UnicodeBlock... unicodeBlocks) {
            this.unicodeBlocks = Set.of(unicodeBlocks);
        }

        public static Language fromCode(String code) {
            return switch (code.toLowerCase()) {
                case "zh_cn" -> ZH_CN;
                case "zh_tw" -> ZH_TW;
                case "ja" -> JA;
                case "ko" -> KO;
                case "fr" -> FR;
                case "de" -> DE;
                case "pt" -> PT;
                case "ru" -> RU;
                case "en" -> EN;
                default -> UNSUPPORTED;
            };
        }

        public static boolean containsLangUnicodeChars(CharSequence sequence, String languageCode) {
            Language language = Language.fromCode(languageCode);
            if (language == Language.UNSUPPORTED) return true;
            return sequence.chars().anyMatch(c -> language.unicodeBlocks.contains(Character.UnicodeBlock.of(c)));
        }
    }

    private static final HashMap<String, String> NAMES_TO_CODE = new HashMap<>();

    private static Locale createLocaleFromCode(String languageCode) {
        String[] parts = languageCode.split("_");
        return parts.length == 2 ? new Locale(parts[0], parts[1]) : new Locale(parts[0]);
    }

    public static String getLanguageNameFromCode(String languageCode) {
        if (languageCode.equals("auto")) return Text.translatable("translator.translation.auto").getString();
        Locale mcLocale = createLocaleFromCode(MinecraftClient.getInstance().options.language);
        Locale lang = createLocaleFromCode(languageCode);
        String res = lang.getDisplayName(mcLocale);
        NAMES_TO_CODE.put(res, languageCode);
        return res;
    }

    public static String getLanguageCodeFromName(String languageName) {
        return NAMES_TO_CODE.getOrDefault(languageName, "auto");
    }
}
