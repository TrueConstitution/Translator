package kgg.translator.translator;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class BaiduTranslatorImpl extends BaiduTranslator{
    @Override
    public void register(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("qps", IntegerArgumentType.integer(0)).executes(
                context -> {
                    setDelayTime(1000 / IntegerArgumentType.getInteger(context, "qps"));
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                }))
                .then(ClientCommandManager.argument("app_id", StringArgumentType.word())
                        .then(ClientCommandManager.argument("app_key", StringArgumentType.word()).executes(context -> {
                            setConfig(StringArgumentType.getString(context, "app_id"), StringArgumentType.getString(context, "app_key"));
                            setDelayTime(1000 / IntegerArgumentType.getInteger(context, "qps"));
                            context.getSource().sendFeedback(Text.literal("OK"));
                            return 0;
                        })));
    }
}
