package kgg.translator.translator;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class BaiduTranslatorImpl extends BaiduTranslator {
    @Override
    public void register(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("qps", IntegerArgumentType.integer(0))
                .executes(
                    context -> {
                        setDelayTime(1000 / IntegerArgumentType.getInteger(context, "qps"));
                        context.getSource().sendFeedback(Text.literal("OK"));
                        return 0;
                    })
                .then(ClientCommandManager.argument("appId", StringArgumentType.word())
                        .then(ClientCommandManager.argument("appKey", StringArgumentType.word()).executes(context -> {
                            setDelayTime(1000 / IntegerArgumentType.getInteger(context, "qps"));
                            setConfig(StringArgumentType.getString(context, "appId"), StringArgumentType.getString(context, "appKey"));
                            context.getSource().sendFeedback(Text.literal("OK"));
                            return 0;
                        }))));
    }

    @Override
    public Runnable registerEntry(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        StringListEntry appIdEntry = entryBuilder.startStrField(Text.literal("AppId"), appId).build();
        StringListEntry appKeyEntry = entryBuilder.startStrField(Text.literal("AppKey"), appKey).build();
        IntegerListEntry qpsEntry = entryBuilder.startIntField(Text.literal("QPS"), delayTime / 1000).build();

        category.add(appIdEntry);
        category.add(appKeyEntry);
        category.add(qpsEntry);

        return () -> {
            setConfig(appIdEntry.getValue(), appKeyEntry.getValue());
            setDelayTime(qpsEntry.getValue() * 1000);
        };
    }
}
