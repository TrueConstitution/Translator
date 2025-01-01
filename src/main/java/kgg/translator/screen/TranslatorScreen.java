package kgg.translator.screen;

import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kgg.translator.TranslatorConfig;
import kgg.translator.TranslatorManager;
import kgg.translator.handler.TranslateHelper;
import kgg.translator.modmenu.ModMenuConfigurable;
import kgg.translator.translator.Translator;
import kgg.translator.util.LanguageUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.*;

public class TranslatorScreen {
    private static String no_secondary_translator;
    private static final Translator dummy = new Translator() {
        @Override
        public String translate(String text, String from, String to) throws IOException {
            return text;
        }
        @Override
        public String getName() {
            return no_secondary_translator;
        }
        @Override
        public void read(JsonObject object) {}
        @Override
        public void write(JsonObject object) {}
        @Override
        public void register(LiteralArgumentBuilder<FabricClientCommandSource> node) {}
    };
    public static Screen buildTranslatorScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.translatable("translator.OptionsScreen.title")).setParentScreen(parent);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("key.categories.translator"));
        List<Runnable> runnables = new ArrayList<>(TranslatorManager.getTranslators().size());

        // 遍历翻译器列表
        for (Translator translator : TranslatorManager.getTranslators()) {
            if (translator instanceof ModMenuConfigurable configurable) {
                // 为每个翻译器创建一个类别
                SubCategoryBuilder tranCategory = entryBuilder.startSubCategory(Text.literal(translator.getName()));
                runnables.add(configurable.registerEntry(entryBuilder, tranCategory));
                category.addEntry(tranCategory.build());
            }
        }
        Map<String, Translator> translatorMap = new HashMap<>();
        no_secondary_translator = Text.translatable("translator.translation.no_secondary_translator").getString();
        Map<String, Translator> translatorMapWithDummy = new HashMap<>(Map.of(no_secondary_translator, dummy));
        for (var t : TranslatorManager.getTranslators()) {
            translatorMap.put(t.getName(), t);
            translatorMapWithDummy.put(t.getName(), t);
        }
        var primary_translator_dropdown = entryBuilder.startDropdownMenu(Text.translatable("translator.translation.primary_translator"),
                        TranslatorManager.getCurrent(), translatorMap::get, t -> Text.literal(t.getName()))
                .setSelections(translatorMap.values())
                .setDefaultValue(TranslatorManager.getCurrent()).setSaveConsumer(TranslatorManager::setTranslator).build();
        category.addEntry(primary_translator_dropdown);

        category.addEntry(entryBuilder.startDropdownMenu(Text.translatable("translator.translation.secondary_translator"),
                        Optional.ofNullable(TranslatorManager.getSecondary()).orElse(translatorMapWithDummy.get(no_secondary_translator)), translatorMapWithDummy::get, t -> Text.literal(t.getName()))
                .setSelections(translatorMapWithDummy.values())
                        .setErrorSupplier(t -> t == primary_translator_dropdown.getValue() ? Optional.of(Text.translatable("translator.error.duplicate_translator")) : Optional.empty())
                .setDefaultValue(translatorMapWithDummy.get(no_secondary_translator)).setSaveConsumer(TranslatorManager::setSecondaryTranslator).build());

        List<String> available_languages = new ArrayList<>();
        List<String> available_languages_no_auto = new ArrayList<>();
        if (TranslatorManager.getCurrent().getLanguageProperties() != null)
            for (var lang_code : TranslatorManager.getCurrent().getLanguageProperties().keySet()) {
                available_languages.add((String) lang_code);
                available_languages_no_auto.add((String) lang_code);
            }
        available_languages_no_auto.remove("auto");
        category.addEntry(entryBuilder.startDropdownMenu(Text.translatable("translator.translation.source"),
                        TranslatorManager.getFrom(), LanguageUtil::getLanguageCodeFromName, c -> Text.literal(LanguageUtil.getLanguageNameFromCode(c)),
                        DropdownMenuBuilder.CellCreatorBuilder.of(c -> Text.literal(LanguageUtil.getLanguageNameFromCode(c))))
                .setSelections(available_languages)
                .setErrorSupplier(c -> available_languages.contains(c) ? Optional.empty() : Optional.of(Text.translatable("translator.translation.does_not_exist")))
                .setDefaultValue("").setSaveConsumer(TranslatorManager::setFrom).build());
        category.addEntry(entryBuilder.startDropdownMenu(Text.translatable("translator.translation.target"),
                        TranslatorManager.getTo(), LanguageUtil::getLanguageCodeFromName, c -> Text.literal(LanguageUtil.getLanguageNameFromCode(c)),
                        DropdownMenuBuilder.CellCreatorBuilder.of(c -> Text.literal(LanguageUtil.getLanguageNameFromCode(c))))
                .setSelections(available_languages_no_auto)
                .setErrorSupplier(c -> available_languages_no_auto.contains(c) ? Optional.empty() : Optional.of(Text.translatable("translator.translation.does_not_exist")))
                .setDefaultValue("").setSaveConsumer(TranslatorManager::setTo).build());

        category.addEntry(entryBuilder.startIntField(Text.translatable("translator.option.splitStyledTextIntoSegments.minStyledSegmentSize"),
                TranslateHelper.getMinStyledSegmentSize())
                .setMin(-1)
                .setDefaultValue(-1).setSaveConsumer(TranslateHelper::setMinStyledSegmentSize).build());

        builder.setSavingRunnable(() -> {
            runnables.forEach(Runnable::run);
            TranslatorConfig.writeFile();
            TranslateHelper.clearCache();
        });
        return builder.build();
    }
}
