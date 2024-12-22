package kgg.translator.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kgg.translator.TranslatorManager;
import kgg.translator.util.EasyProperties;
import kgg.translator.util.LanguageLocalizer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class LangArgumentType implements ArgumentType<String> {
    public static LangArgumentType lang() {
        return new LangArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readQuotedString();
    }

    public static String getLanguage(CommandContext<FabricClientCommandSource> context, String name) {
        String string = LanguageLocalizer.getLanguageCodeFromName(context.getArgument(name, String.class));
        EasyProperties properties = TranslatorManager.getCurrent().getLanguageProperties();
        if (!properties.containsKey(string)) return string;
        String lang = properties.getProperty(string);
        if (lang != null) return lang;
        return string;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            EasyProperties properties = TranslatorManager.getCurrent().getLanguageProperties();
            if (properties == null) return Suggestions.empty();
            return CommandSource.suggestMatching(properties.keySet().stream().map(c -> '"' + LanguageLocalizer.getLanguageNameFromCode((String) c) + '"').toList(), builder);
        }
        return Suggestions.empty();
    }
}
