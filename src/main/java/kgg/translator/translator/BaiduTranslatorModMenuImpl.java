package kgg.translator.translator;

import kgg.translator.modmenu.ModMenuConfigurable;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;

public class BaiduTranslatorModMenuImpl extends BaiduTranslatorImpl implements ModMenuConfigurable {
    @Override
    public Runnable registerEntry(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        StringListEntry appIdEntry = entryBuilder.startStrField(Text.literal("AppId"), appId).build();
        StringListEntry appKeyEntry = entryBuilder.startStrField(Text.literal("AppKey"), appKey).build();
        IntegerListEntry qpsEntry = entryBuilder.startIntField(Text.literal("QPS"), 1000 / delayTime).build();

        category.add(appIdEntry);
        category.add(appKeyEntry);
        category.add(qpsEntry);

        return () -> {
            setConfig(appIdEntry.getValue(), appKeyEntry.getValue());
            setDelayTime(1000 / qpsEntry.getValue());
        };
    }
}
