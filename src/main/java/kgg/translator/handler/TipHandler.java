package kgg.translator.handler;

import kgg.translator.TranslatorManager;
import kgg.translator.exception.TranslateException;
import kgg.translator.util.StringUtil;
import kgg.translator.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
/**
 * 负责翻译工具提示
 */
public class TipHandler {
    public static boolean drawTranslateText = false;  // 需要画翻译
    private static OrderedText[] translatedOrderedText;  // 翻译后的文本
    private static int translatedTextCount;
    private static int noTranslateCount;
    private static List<Text> lastText;  // 上一次
    private static int time = 0;  // 时间

    /**
     * 0.5秒后翻译文本
     */
    public static void handle(DrawContext drawContext, List<Text> text, int mouseX, int mouseY, float time1) {
        if (!text.equals(lastText)) {
            resetState(text);
        } else {
            // 0.5秒后翻译
            if (time > MinecraftClient.getInstance().getCurrentFps() * time1) {
                time = Integer.MAX_VALUE;
                if (translatedOrderedText == null) {
                    startTranslation(text);
                } else if (translatedTextCount == text.size() && noTranslateCount != text.size()) {
                    drawTranslateText = true;  // 有翻译内容
                }
            } else {
                time++;
            }
        }
    }

    private static void resetState(List<Text> text) {
        time = 0;
        lastText = text;
        translatedTextCount = 0;
        translatedOrderedText = null;
        drawTranslateText = false;
    }

    private static void startTranslation(List<Text> texts) {
        OrderedText[] temp = translatedOrderedText = new OrderedText[texts.size()];
        translatedTextCount = 0;
        noTranslateCount = 0;

        for (int i = 0; i < texts.size(); i++) {
            Text text = texts.get(i);
            int finalI = i;

            CompletableFuture.supplyAsync(() -> {
                if (TextUtil.isSystemText(text)) {
                    return text;
                }
                return TranslateHelper.translateNoWait(text);
            }).thenApply(trans -> {
                temp[finalI] = trans.asOrderedText();
                if (StringUtil.equals(trans.getString(), text.getString())) {
                    noTranslateCount++;
                }
                translatedTextCount++;
                return null;
            });
        }
    }

    public static OrderedText[] getTranslatedOrderedText() {
        return translatedOrderedText;
    }

    public record SidebarEntry(Text name, Text score, int scoreWidth) {
    }
}
