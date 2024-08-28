package kgg.translator.translator;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class YouDaoTranslatorImpl extends YouDaoTranslator {
    @Override
    public void register(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("app_key", StringArgumentType.word())
                .then(ClientCommandManager.argument("app_secret", StringArgumentType.word()).executes(context -> {
                    setConfig(StringArgumentType.getString(context, "app_key"), StringArgumentType.getString(context, "app_secret"));
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                })));
    }
}
