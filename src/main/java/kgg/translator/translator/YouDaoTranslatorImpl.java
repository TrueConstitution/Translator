package kgg.translator.translator;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class YouDaoTranslatorImpl extends YouDaoTranslator {
    @Override
    public void register(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("appId", StringArgumentType.word())
                .then(ClientCommandManager.argument("appKey", StringArgumentType.word()).executes(context -> {
                    setConfig(StringArgumentType.getString(context, "appId"), StringArgumentType.getString(context, "appKey"));
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                })));
    }

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
