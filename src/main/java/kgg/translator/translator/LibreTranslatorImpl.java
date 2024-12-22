package kgg.translator.translator;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class LibreTranslatorImpl extends LibreTranslator {
    @Override
    public void register(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("libre_url", StringArgumentType.word()).executes(context -> {
                    setConfig(StringArgumentType.getString(context, "libre_url"), "");
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                })
                .then(ClientCommandManager.argument("api_key", StringArgumentType.word()).executes(context -> {
                    setConfig(StringArgumentType.getString(context, "libre_url"), StringArgumentType.getString(context, "api_key"));
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                })));
    }
}
