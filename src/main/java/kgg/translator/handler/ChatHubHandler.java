package kgg.translator.handler;

import kgg.translator.TranslatorManager;
import kgg.translator.option.ChatOption;
import kgg.translator.exception.TranslateException;
import kgg.translator.util.StringUtil;
import kgg.translator.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ChatHubHandler {
    private static final Logger LOGGER = LogManager.getLogger(ChatHubHandler.class);

    public static void preHandle(MutableText text) {
        if (TextUtil.isSystemText(text)) {
            return;
        }
        if (StringUtil.isBlank(text.getString())) {  // 不翻译空行
            return;
        }
        if (ChatOption.chatTip.isEnable()) {  // 添加翻译提示
            text.siblings = new ArrayList<>(text.siblings);
            text.append(" ").append(Text.literal("[翻译]").setStyle(Style.EMPTY
                    .withColor(TextColor.fromRgb(65522))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击翻译")))
                    .withClickEvent(new TranslateClickEvent(text))));
        }
        if (ChatOption.autoChat.isEnable()) {  // 如果是自动翻译则处理
            if (ChatOption.chatTip.isEnable()) {
                handleWithTranslatingTip(text);
            } else {
                handle(text);
            }
        }
    }

    private static final Text TRANSLATING_TIP = Text.literal("[翻译中]").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(2259711)));

    public static void handleWithTranslatingTip(MutableText text) {
        text.siblings.remove(text.siblings.size() - 1);  // 移除翻译提示

        String s = StringUtil.strip(text.getString());
        text.siblings.add(TRANSLATING_TIP);  // 添加翻译中提示
        MinecraftClient.getInstance().inGameHud.getChatHud().refresh();

        // 请求
        CompletableFuture.runAsync(() -> {
            try {
                String result = TranslatorManager.cachedTranslate(s);
                text.siblings.set(text.siblings.size() - 1, createResultText(result, text));
            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            String err = throwable.getMessage();
            text.siblings.set(text.siblings.size() - 1, createErrorText(err, text));
            return null;
        }).handle((unused, throwable) -> {
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().inGameHud.getChatHud().refresh());
            return null;
        });
    }

    public static void handle(MutableText text) {

        String s = StringUtil.strip(text.getString());
        // 请求
        CompletableFuture.runAsync(() -> {
            try {
                String result = TranslatorManager.cachedTranslate(s);
                text.append(" ").append(createResultText(result, text));
            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            String err = throwable.getMessage();
            text.append(" ").append(createErrorText(err, text));
            return null;
        }).handle((unused, throwable) -> {
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().inGameHud.getChatHud().refresh());
            return null;
        });
    }

    private static final HoverEvent TRANSLATE_HOVER_EVENT = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击重新翻译"));

    private static Text createErrorText(String err, MutableText text) {
        return Text.literal("[" + err + "]").setStyle(Style.EMPTY
                .withColor(TextColor.fromRgb(13378339))
                .withHoverEvent(TRANSLATE_HOVER_EVENT)
                .withClickEvent(new TranslateClickEvent(text)));
    }

    private static Text createResultText(String result, MutableText text) {
        return Text.literal(result).setStyle(Style.EMPTY
                .withColor(TextColor.fromRgb(3145516))
                .withHoverEvent(TRANSLATE_HOVER_EVENT)
                .withClickEvent(new TranslateClickEvent(text)));
    }

    public static class TranslateClickEvent extends ClickEvent {
        public MutableText text;
        public TranslateClickEvent(MutableText text) {
            super(null, "");
            this.text = text;
        }
    }

}
