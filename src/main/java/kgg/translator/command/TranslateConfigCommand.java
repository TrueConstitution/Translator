package kgg.translator.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import kgg.translator.TranslatorConfig;
import kgg.translator.TranslatorManager;
import kgg.translator.handler.TranslateHelper;
import kgg.translator.translator.Translator;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.*;

public class TranslateConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("transconfig");

        // /trans-config language <from> [<to>]
        root.then(ClientCommandManager.literal("language")
                .executes(TranslateConfigCommand::queryLanguage)
                .then(ClientCommandManager.argument("from", LangArgumentType.lang())
                        .executes(context -> {
                            String from = LangArgumentType.getLanguage(context, "from");
                            TranslatorManager.setFrom(from);
                            return queryLanguage(context);
                        })
                        .then(ClientCommandManager.argument("to", LangArgumentType.lang())
                                .executes(context -> {
                                    String from = LangArgumentType.getLanguage(context, "from");
                                    String to = LangArgumentType.getLanguage(context, "to");
                                    TranslatorManager.setFrom(from);
                                    TranslatorManager.setTo(to);
                                    return queryLanguage(context);
                                }))));
        // /trans-config translator <translator> ...
        LiteralArgumentBuilder<FabricClientCommandSource> selectNode = ClientCommandManager.literal("translator")
                .executes(TranslateConfigCommand::queryTranslator);
        TranslatorManager.getTranslators().forEach(translator -> {
            LiteralArgumentBuilder<FabricClientCommandSource> subNode = ClientCommandManager.literal(translator.getName())
                    .executes(context -> {
                        boolean b = TranslatorManager.setTranslator(translator);
                        int a = queryTranslator(context);
                        if (!b) {
                            context.getSource().sendError(Text.translatable(""));
                        }
                        return a;
                    });
            translator.register(subNode);
            selectNode.then(subNode);
        });
        root.then(selectNode);

        // /trans-config save
        root.then(ClientCommandManager.literal("save")
                .executes(context -> {
                    if (TranslatorConfig.writeFile()) {
                        context.getSource().sendFeedback(Text.literal("OK"));
                    } else {
                        context.getSource().sendError(Text.literal("Failed to save config"));
                    }
                    return 0;
                }));

        // /trans-config load <string>
        root.then(ClientCommandManager.literal("load")
                .then(ClientCommandManager.argument("json", StringArgumentType.greedyString())
                        .executes(context -> {
                            String str = StringArgumentType.getString(context, "json");
                            try {
                                JsonObject object = JsonParser.parseString(str).getAsJsonObject();
                                boolean read = TranslatorConfig.readConfig(object);
                                if (read) {
                                    context.getSource().sendFeedback(Text.literal("OK"));
                                } else {
                                    context.getSource().sendError(Text.literal("Failed to load config"));
                                }
                                return 0;
                            } catch (JsonSyntaxException e) {
                                context.getSource().sendError(Text.literal("Invalid json"));
                                return 0;
                            }
                        })));

        // /trans-config clearcache
        root.then(ClientCommandManager.literal("clearcache")
                .executes(context -> {
                    TranslatorManager.clearCache();
                    TranslateHelper.clearCache();
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                }));

        // /trans-config config-txt
        root.then(ClientCommandManager.literal("config").executes(context -> {
            JsonObject object = new JsonObject();
            TranslatorConfig.writeConfig(object);
            String txt = object.toString();
            MutableText message = Text.literal(txt);
            message.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, txt)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("translator.translate.copy_result"))));
            context.getSource().sendFeedback(message);
            return 0;
        }));

        dispatcher.register(root);
    }

    private static int queryLanguage(CommandContext<FabricClientCommandSource> context) {
        Text message = Text.translatable("translator.current_translation", TranslatorManager.getFrom(), TranslatorManager.getTo());
        context.getSource().sendFeedback(message);
        return 0;
    }

    private static int queryTranslator(CommandContext<FabricClientCommandSource> context) {
        Translator translator = TranslatorManager.getCurrent();
        Text message = Text.translatable("translator.current_translator", translator.getName());
        context.getSource().sendFeedback(message);
        if (translator.isConfigured()) {
                message = Text.translatable("translator.translator_configured",translator.getName()).setStyle(Style.EMPTY.withColor(0x00ff00));
            } else {
                message = Text.translatable("translator.translator_not_configured",translator.getName()).setStyle(Style.EMPTY.withColor(0xff0000));
            }
            context.getSource().sendFeedback(message);
        return 0;
    }

}
