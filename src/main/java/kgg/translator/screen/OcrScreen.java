package kgg.translator.screen;

import kgg.translator.ocrtrans.ResRegion;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OcrScreen extends Screen {
    private final Screen parent;

    public void setResRegions(ResRegion[] resRegions) {
        this.resRegions = resRegions;
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
    private static final MutableText error = Text.literal("没有识别到文字或识别错误");
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, esc, width - textRenderer.getWidth(esc), height - textRenderer.fontHeight, 0xFFFFFF, true);
        if (resRegions == null) {
            context.drawCenteredTextWithShadow(textRenderer, translatingText, width / 2, height / 2, 0xFFFFFF);
        } else if (resRegions.length == 0) {
            context.drawCenteredTextWithShadow(textRenderer, error, width / 2, height / 2,  0xFF0000);
        } else {
            for (ResRegion resRegion : resRegions) {
                context.fill(resRegion.x(), resRegion.y(), resRegion.x() + resRegion.w(), resRegion.y() + resRegion.h(), 0x4cff7272);

                MatrixStack matrices = context.getMatrices();
                matrices.push();
                // TODO: 2024/8/28 位置还有点小问题
                float textWidth = textRenderer.getWidth(resRegion.dst());
                float textHeight = textRenderer.fontHeight;
                float scale = Math.min(resRegion.w() / textWidth, resRegion.h() / textHeight);
                float textWidth2 = (textWidth * scale);
                float textHeight2 = (textHeight * scale);
                float textX = (float) resRegion.x() + ((resRegion.w() - textWidth2) / 2);
                float textY = (float) resRegion.y() + ((resRegion.h() - textHeight2) / 2);

                matrices.scale(scale, scale, scale);
                context.drawText(textRenderer, resRegion.dst(), (int) (textX / scale), (int) (textY / scale), 0x1bb7ff, true);

                matrices.pop();
            }
        }
    }
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (resRegions == null || resRegions.length == 0) {
            super.renderBackground(context, mouseX, mouseY, 0.01f);
        }
    }
}
