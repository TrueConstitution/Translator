package kgg.translator.handler;

import kgg.translator.TranslatorManager;
import kgg.translator.exception.TranslateException;
import kgg.translator.util.StringUtil;
import kgg.translator.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 负责翻译工具提示
 */
public class TooltipHandler {
    private static final Logger LOGGER = LogManager.getLogger(TooltipHandler.class);

    public static boolean drawTranslateText = false;  // 需要画翻译
    private static TooltipComponent[] translatedOrderedText;  // 翻译后的文本
    private static AtomicInteger translatedTextCount;
    private static AtomicInteger noTranslateCount;
    private static List<Text> lastText;  // 上一次
    private static int time = 0;  // 时间

    /**
     * 0.5秒后翻译文本
     */
    public static void preHandle(DrawContext drawContext, List<Text> text, int mouseX, int mouseY) {
        if (!text.equals(lastText)) {
            time = 0;
            lastText = text;
            translatedTextCount = null;
            translatedOrderedText = null;
            drawTranslateText = false;

        } else {
            // 0.5秒后翻译
            if (time > MinecraftClient.getInstance().getCurrentFps() * 0.5) {
                time = Integer.MAX_VALUE;
                // 如果没翻译过
                if (translatedOrderedText == null) {
                    // 在异步中翻译
                    TooltipComponent[] temp = translatedOrderedText = new TooltipComponent[text.size()];
                    AtomicInteger tempTranslatedCount = translatedTextCount = new AtomicInteger();
                    AtomicInteger tempNoTranslateCount = noTranslateCount = new AtomicInteger();
                    Text t;
                    for (int i = 0; i < text.size(); i++) {
                        t = text.get(i);
                        String string = t.getString();
                        int finalI = i;
                        Text finalT = t;

                        CompletableFuture.supplyAsync(() -> {
                            // 如果有翻译，则直接添加
                            TranslatableTextContent content = TextUtil.getOnlyTranslateText(finalT);
                            if (content != null) {
                                if (Language.getInstance().hasTranslation(content.getKey())) {
                                    return string;
                                }
                            }

                            // 获取
                            try {
                                return TranslatorManager.cachedTranslate(string);
                            } catch (TranslateException e) {
                                LOGGER.error("translate failed", e);
                                return string;
                            }
                        }).thenApply(trans -> {
                            temp[finalI] = getTooltipComponent(trans, finalT);
                            if (StringUtil.equals(trans, string)) {
                                tempNoTranslateCount.getAndIncrement();
                            }
                            tempTranslatedCount.getAndIncrement();
                            return null;
                        });
                    }
                } else {
                    // 如果全部翻译完成
                    if (translatedTextCount.get() == text.size()) {
                        if (noTranslateCount.get() != text.size()) {
                            drawTranslateText = true;
                        }
                    }
                }
            } else {
                time++;
            }
        }
    }

    private static TooltipComponent getTooltipComponent(String s, Text text) {
        return TooltipComponent.of(OrderedText.styledForwardsVisitedString(s, text.getStyle()));
    }

    public static TooltipComponent[] getTranslatedOrderedText() {
        return translatedOrderedText;
    }
}
