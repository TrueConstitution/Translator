package kgg.translator.screen;

import kgg.translator.TranslatorConfig;
import kgg.translator.TranslatorManager;
import kgg.translator.modmenu.ModMenuConfigurable;
import kgg.translator.translator.Translator;
import kgg.translator.util.LanguageLocalizer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TranslatorScreen {
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
        List<String> available_languages = TranslatorManager.getCurrent().getLanguageProperties().keySet().stream().map(o -> (String) o).toList();
        category.addEntry(entryBuilder.startDropdownMenu(Text.translatable("translator.translation.source"),
                        TranslatorManager.getFrom(), LanguageLocalizer::getLanguageCodeFromName, c -> Text.literal(LanguageLocalizer.getLanguageNameFromCode(c)),
                        DropdownMenuBuilder.CellCreatorBuilder.of(c -> Text.literal(LanguageLocalizer.getLanguageNameFromCode(c))))
                .setSelections(available_languages)
                .setErrorSupplier(c -> available_languages.contains(c) ? Optional.empty() : Optional.of(Text.translatable("translator.translation.does_not_exist")))
                .setDefaultValue("").setSaveConsumer(TranslatorManager::setFrom).build());
        List<String> available_languages_no_auto = available_languages.stream().filter(c -> !"auto".equals(c)).toList();
        category.addEntry(entryBuilder.startDropdownMenu(Text.translatable("translator.translation.target"),
                        TranslatorManager.getTo(), LanguageLocalizer::getLanguageCodeFromName, c -> Text.literal(LanguageLocalizer.getLanguageNameFromCode(c)),
                        DropdownMenuBuilder.CellCreatorBuilder.of(c -> Text.literal(LanguageLocalizer.getLanguageNameFromCode(c))))
                .setSelections(available_languages_no_auto)
                .setErrorSupplier(c -> available_languages_no_auto.contains(c) ? Optional.empty() : Optional.of(Text.translatable("translator.translation.does_not_exist")))
                .setDefaultValue("").setSaveConsumer(TranslatorManager::setTo).build());

        builder.setSavingRunnable(() -> {
            runnables.forEach(Runnable::run);
            TranslatorConfig.writeFile();
        });
        return builder.build();
    }
}
