package kgg.translator.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import kgg.translator.config.Option;
import kgg.translator.config.TranslatorConfig;
import kgg.translator.TranslatorManager;
import kgg.translator.handler.TranslateHelper;
import kgg.translator.translator.Translator;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class TranslateConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("transconfig");

        // TODO: 2024/8/29 语言补全
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

        // /trans-config clearcache
        root.then(ClientCommandManager.literal("clearcache")
                .executes(context -> {
                    TranslatorManager.clearCache();
                    TranslateHelper.clearCache();
                    context.getSource().sendFeedback(Text.literal("OK"));
                    return 0;
                }));
        // TODO: 2024/8/28 更好的了解
        // /trans-config <option> true/false
        Option.getOptions().forEach(option -> {
            root.then(ClientCommandManager.literal(option.name)
                    .executes(context -> queryOption(context, option))
                    .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
                            .executes(context -> {
                                boolean enable = BoolArgumentType.getBool(context, "enable");
                                option.setEnable(enable);
                                return queryOption(context, option);
                            })));
        });
//        // /trans-config chathud add-tip <true/false>
//        // /trans-config chathud auto <true/false>
//        root.then(ClientCommandManager.literal("chathud")
//                .then(ClientCommandManager.literal("auto")
//                        .executes(TranslateConfigCommand::queryChatHudAuto)
//                        .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
//                                .executes(context -> {
//                                    boolean enable = BoolArgumentType.getBool(context, "enable");
//                                    Option.setAutoChatHud(enable);
//                                    return queryChatHudAuto(context);
//                                })))
//                .then(ClientCommandManager.literal("add-tip")
//                        .executes(TranslateConfigCommand::queryChatHudAddTip)
//                        .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
//                                .executes(context -> {
//                                    boolean enable = BoolArgumentType.getBool(context, "enable");
//                                    Option.setAddChatHudTip(enable);
//                                    return queryChatHudAddTip(context);
//                                }))));
//
//        // /trans-config tooltip <true/false>
//        root.then(ClientCommandManager.literal("tooltip")
//                .executes(TranslateConfigCommand::queryTooltip)
//                .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
//                        .executes(context -> {
//                            boolean enable = BoolArgumentType.getBool(context, "enable");
//                            Option.setAutoToolTip(enable);
//                            return queryTooltip(context);
//                        })));
//
//
//        // /trans-config screentext <true/false>
//        root.then(ClientCommandManager.literal("screentext")
//                .executes(TranslateConfigCommand::queryScreenText)
//                .then(ClientCommandManager.argument("enable", BoolArgumentType.bool())
//                        .executes(context -> {
//                            boolean enable = BoolArgumentType.getBool(context, "enable");
//                            Option.setAutoScreenText(enable);
//                            return queryScreenText(context);
//                        })));


        dispatcher.register(root);
    }

    private static int queryOption(CommandContext<FabricClientCommandSource> context, Option option) {
        if (option.isEnable()) {
            context.getSource().sendFeedback(Text.literal("已开启"));
        } else {
            context.getSource().sendFeedback(Text.literal("已关闭"));
        }
        return 0;
    }

//    private static int queryScreenText(CommandContext<FabricClientCommandSource> context) {
//        Text message = Text.literal("当前屏幕文字自动翻译为%s".formatted(Option.isAutoScreenText()));
//        context.getSource().sendFeedback(message);
//        return 0;
//    }
//
//    private static int queryTooltip(CommandContext<FabricClientCommandSource> context) {
//        Text message = Text.literal("当前工具提示翻译为%s".formatted(Option.isAutoToolTip()));
//        context.getSource().sendFeedback(message);
//        return 0;
//    }
//
//    private static int queryChatHudAddTip(CommandContext<FabricClientCommandSource> context) {
//        Text message = Text.literal("当前聊天框添加翻译提示为%s".formatted(Option.isAddChatHudTip()));
//        context.getSource().sendFeedback(message);
//        return 0;
//    }
//
//    private static int queryChatHudAuto(CommandContext<FabricClientCommandSource> context) {
//        Text message = Text.literal("当前聊天框自动翻译为%s".formatted(Option.isAutoChatHud()));
//        context.getSource().sendFeedback(message);
//        return 0;
//    }

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
