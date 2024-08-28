package kgg.translator.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import kgg.translator.CacheManager;
import kgg.translator.Options;
import kgg.translator.TranslatorConfig;
import kgg.translator.TranslatorManager;
import kgg.translator.translator.Translator;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class TranslateConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("transconfig");

        // /trans-config language <from> [<to>]
        root.then(ClientCommandManager.literal("language")
                .executes(TranslateConfigCommand::queryLanguage)
                .then(ClientCommandManager.argument("from", StringArgumentType.word())
                        .executes(context -> {
                            String from = StringArgumentType.getString(context, "from");
                            TranslatorManager.setDefaultFrom(from);
                            return queryLanguage(context);
                        })
                        .then(ClientCommandManager.argument("to", StringArgumentType.word())
                                .executes(context -> {
                                    String from = StringArgumentType.getString(context, "from");
                                    String to = StringArgumentType.getString(context, "to");
                                    TranslatorManager.setDefaultFrom(from);
                                    TranslatorManager.setDefaultTo(to);
                                    return queryLanguage(context);
                                }))));
        // /trans-config translator <translator> ...
        LiteralArgumentBuilder<FabricClientCommandSource> selectNode = ClientCommandManager.literal("translator")
                .executes(TranslateConfigCommand::queryTranslator);
        TranslatorManager.getTranslators().forEach(translator -> {
            LiteralArgumentBuilder<FabricClientCommandSource> subNode = ClientCommandManager.literal(translator.getName())
                    .executes(context -> {
                        boolean b = TranslatorManager.setCurrentTranslator(translator);
                        int a = queryTranslator(context);
                        if (b) {
                            context.getSource().sendFeedback(Text.literal("记得修改语言哦"));
                        }
                        return a;
                    });
            translator.register(subNode);
            selectNode.then(subNode);
        });
        root.then(selectNode);

        // /trans-config chathud add-tip <true/false>
        // /trans-config chathud auto <true/false>
        root.then(ClientCommandManager.literal("chathud")
                .then(ClientCommandManager.literal("auto")
                        .executes(TranslateConfigCommand::queryChatHudAuto)
                        .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean enable = BoolArgumentType.getBool(context, "enable");
                                    Options.setAutoChatHud(enable);
                                    return queryChatHudAuto(context);
                                })))
                .then(ClientCommandManager.literal("add-tip")
                        .executes(TranslateConfigCommand::queryChatHudAddTip)
                        .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean enable = BoolArgumentType.getBool(context, "enable");
                                    Options.setAddChatHudTip(enable);
                                    return queryChatHudAddTip(context);
                                }))));

        // /trans-config tooltip <true/false>
        root.then(ClientCommandManager.literal("tooltip")
                .executes(TranslateConfigCommand::queryTooltip)
                .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean enable = BoolArgumentType.getBool(context, "enable");
                            Options.setAutoToolTip(enable);
                            return queryTooltip(context);
                        })));


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
                            try {
                                String json = StringArgumentType.getString(context, "json");
                                JsonObject object = (JsonObject) JsonParser.parseString(json);
                                boolean read = TranslatorConfig.read(object);
                                assert read;
                                context.getSource().sendFeedback(Text.literal("OK"));
                            } catch (Exception e) {
                                context.getSource().sendError(Text.literal("Failed to load config"));
                            }
                            return 0;
                        })));

        // /trans-config screentext <true/false>
        root.then(ClientCommandManager.literal("screentext")
                .executes(TranslateConfigCommand::queryScreenText)
                .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean enable = BoolArgumentType.getBool(context, "enable");
                            Options.setAutoScreenText(enable);
                            return queryScreenText(context);
                        })));

        // /trans-config clearcache
        root.then(ClientCommandManager.literal("clearcache")
                .executes(context -> {
                    CacheManager.clearCache();
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                }));
        dispatcher.register(root);
    }

    private static int queryScreenText(CommandContext<FabricClientCommandSource> context) {
        Text message = Text.literal("当前屏幕文字自动翻译为%s".formatted(Options.isAutoScreenText()));
        context.getSource().sendFeedback(message);
        return 0;
    }

    private static int queryTooltip(CommandContext<FabricClientCommandSource> context) {
        Text message = Text.literal("当前工具提示翻译为%s".formatted(Options.isAutoToolTip()));
        context.getSource().sendFeedback(message);
        return 0;
    }

    private static int queryChatHudAddTip(CommandContext<FabricClientCommandSource> context) {
        Text message = Text.literal("当前聊天框添加翻译提示为%s".formatted(Options.isAddChatHudTip()));
        context.getSource().sendFeedback(message);
        return 0;
    }

    private static int queryChatHudAuto(CommandContext<FabricClientCommandSource> context) {
        Text message = Text.literal("当前聊天框自动翻译为%s".formatted(Options.isAutoChatHud()));
        context.getSource().sendFeedback(message);
        return 0;
    }

    private static int queryLanguage(CommandContext<FabricClientCommandSource> context) {
        Text message = Text.literal("当前从%s翻译成%s".formatted(TranslatorManager.getDefaultFrom(), TranslatorManager.getDefaultTo()));
        context.getSource().sendFeedback(message);
        return 0;
    }

    private static int queryTranslator(CommandContext<FabricClientCommandSource> context) {
        Translator translator = TranslatorManager.getCurrentTranslator();
        Text message = Text.literal("当前使用的翻译器为%s".formatted(translator));
        context.getSource().sendFeedback(message);
        if (translator.isConfigured()) {
                message = Text.literal("%s已配置".formatted(translator));
            } else {
                message = Text.literal("%s未配置".formatted(translator));
            }
            context.getSource().sendFeedback(message);
        return 0;
    }

}
