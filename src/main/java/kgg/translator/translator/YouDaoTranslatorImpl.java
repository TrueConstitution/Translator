package kgg.translator.translator;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
}
