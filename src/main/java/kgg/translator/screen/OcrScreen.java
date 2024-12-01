package kgg.translator.screen;

import kgg.translator.ocrtrans.ResRegion;
import kgg.translator.util.TextUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class OcrScreen extends Screen {
    private final Screen parent;

    public void setResRegions(ResRegion[] resRegions) {
        this.resRegions = resRegions;
    }

    public void setError(String error) {
        this.error = Text.literal(error);
    }

    private ResRegion[] resRegions;

    public OcrScreen(Screen parent) {
        super(Text.literal("Ocr"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (resRegions != null) {
            this.client.setScreen(this.parent);
        }
    }

    private static final MutableText translatingText = Text.literal("翻译中");
    private static final MutableText esc = Text.literal("按Esc取消");
    private MutableText error;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, esc, width - textRenderer.getWidth(esc), height - textRenderer.fontHeight, 0xFFFFFF, true);
        if (error != null) {
            context.drawCenteredTextWithShadow(textRenderer, error, width / 2, height / 2, 0xFF0000);
        } else if (resRegions == null) {
            context.drawCenteredTextWithShadow(textRenderer, translatingText, width / 2, height / 2, 0xFFFFFF);
        } else {
            for (ResRegion resRegion : resRegions) {
                context.fill(resRegion.x(), resRegion.y(), resRegion.x() + resRegion.w(), resRegion.y() + resRegion.h(), 0x96ff7272);

                MatrixStack matrices = context.getMatrices();
                matrices.push();

                String text = resRegion.dst();
                float textWidth = textRenderer.getWidth(text);
                float textHeight = textRenderer.fontHeight;

                // 计算初始缩放比例，确保文本可以适应区域，添加最小缩放限制
                float scale = Math.min(resRegion.w() / textWidth, resRegion.h() / textHeight);
                scale = Math.max(scale, 1.0f);  // 最小缩放比例为1.0，避免文本过小

                // 如果区域高度较大，尝试将文字分多行显示
                if (resRegion.h() > textHeight * 2) {
                    List<OrderedText> lines = ChatMessages.breakRenderedChatMessageLines(Text.literal(text), resRegion.w(), textRenderer);
                    float totalHeight = lines.size() * textHeight;

                    // 获取文本渲染器的行间距
                    float lineSpacing = textRenderer.fontHeight+1;
                    // 调整缩放比例来适应多行文本，这里考虑行间距因素
                    scale = Math.min(resRegion.w() / textWidth, resRegion.h() / (totalHeight + (lines.size() - 1) * lineSpacing));
                    scale = Math.max(scale, 1.0f);  // 最小缩放比例为1.0

                    // 计算每行文本在垂直方向上可用于布局的“有效高度范围”，这里考虑行间距因素
                    float lineVerticalRange = (resRegion.h() - (lines.size() * (textHeight + lineSpacing) * scale)) / (lines.size() + 1);
                    float textY = resRegion.y() + lineVerticalRange + (textHeight * scale) / 2;

                    matrices.scale(scale, scale, scale);
                    for (OrderedText line : lines) {
                        float lineWidth = textRenderer.getWidth(TextUtil.getString(line));
                        float textX = resRegion.x() + (resRegion.w() - lineWidth * scale) / 2;
                        context.drawText(textRenderer, line, (int) (textX / scale), (int) (textY / scale), 0x1bb7ff, true);
                        textY += lineVerticalRange + lineSpacing * scale;
                    }
                } else {
                    // 计算文本显示位置和缩放
                    float textWidth2 = textWidth * scale;
                    float textHeight2 = textHeight * scale;
                    float textX = resRegion.x() + (resRegion.w() - textWidth2) / 2;  // 水平居中
                    float textY = resRegion.y() + (resRegion.h() - textHeight2) / 2; // 垂直居中

                    matrices.scale(scale, scale, scale);
                    context.drawText(textRenderer, text, (int) (textX / scale), (int) (textY / scale), 0x1bb7ff, true);
                }

                matrices.pop();
            }
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void renderBackground(DrawContext context) {
        if (resRegions == null || resRegions.length == 0) {
            super.renderBackground(context);
        }
    }
}
