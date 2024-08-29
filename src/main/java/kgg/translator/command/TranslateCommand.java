package kgg.translator.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import kgg.translator.TranslatorManager;
import kgg.translator.exception.TranslateException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class TranslateCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("translate")
                .then(ClientCommandManager.argument("text", StringArgumentType.greedyString())
                        .executes(context -> {
                            Text message;
                            try {
                                String result = TranslatorManager.cachedTranslate(StringArgumentType.getString(context, "text"));
                                message = Text.literal(result);
                                context.getSource().sendFeedback(message);
                            } catch (TranslateException e) {
                                message = Text.literal(e.getMessage());
                                context.getSource().sendError(message);
                            }
                            return 0;
                        })));
    }
}
