package kgg.translator.translator;

import kgg.translator.modmenu.ModMenuConfigurable;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;

public class YouDaoTranslatorModMenuImpl extends YouDaoTranslatorImpl implements ModMenuConfigurable {
    @Override
    public Runnable registerEntry(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        StringListEntry appIdEntry = entryBuilder.startStrField(Text.literal("AppId"), appId).setSaveConsumer(s -> {
            System.out.println("appid" + s);
        }).build();
        StringListEntry appKeyEntry = entryBuilder.startStrField(Text.literal("AppKey"), appKey).setSaveConsumer(s -> {
            System.out.println("appkey" + s);
        }).build();

        category.add(appIdEntry);
        category.add(appKeyEntry);

        return () -> {
            setConfig(appIdEntry.getValue(), appKeyEntry.getValue());
        };
    }
}
