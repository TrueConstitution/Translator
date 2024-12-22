package kgg.translator.translator;

import kgg.translator.modmenu.ModMenuConfigurable;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;

public class LibreTranslatorModMenuImpl extends LibreTranslatorImpl implements ModMenuConfigurable {
    @Override
    public Runnable registerEntry(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        StringListEntry libre_url_entry = entryBuilder.startStrField(Text.literal("LibreTranslate URL"), libre_url).build();
        StringListEntry api_key_entry = entryBuilder.startStrField(Text.literal("API Key (if required)"), api_key).build();

        category.add(libre_url_entry);
        category.add(api_key_entry);

        return () -> setConfig(libre_url_entry.getValue(), api_key_entry.getValue());
    }
}
