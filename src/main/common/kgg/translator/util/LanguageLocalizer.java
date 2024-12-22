package kgg.translator.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Locale;

public class LanguageLocalizer {
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
