package kgg.translator.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import kgg.translator.TranslatorConfig;
import kgg.translator.TranslatorManager;
import kgg.translator.translator.Translator;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> {
            ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.literal("翻译配置")).setParentScreen(p);
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory category = builder.getOrCreateCategory(Text.literal("翻译器"));
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

            builder.setSavingRunnable(() -> {
                runnables.forEach(Runnable::run);
                TranslatorConfig.writeFile();
            });
            return builder.build();
        };
    }
}
