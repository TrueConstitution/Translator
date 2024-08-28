package kgg.translator.screen;

import kgg.translator.ocr.ResRegion;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

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
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        MatrixStack matrices = context.getMatrices();
//        matrices.scale();
        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, esc, width - textRenderer.getWidth(esc), height - textRenderer.fontHeight, 0xFFFFFF, true);
        if (resRegions == null) {
//            renderBackground(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer, translatingText, width / 2, height / 2, 0xFFFFFF);
//            context.drawText(textRenderer, translatingText, width / 2, height / 2, 0xFFFFFF, true);
        } else {
            for (ResRegion resRegion : resRegions) {
//                textRenderer.draw()
                // 蓝色
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

                context.fill(resRegion.x(), resRegion.y(), resRegion.x() + resRegion.w(), resRegion.y() + resRegion.h(), 0x59ffffff);

                matrices.scale(scale, scale, scale);
                context.drawText(textRenderer, resRegion.dst(), (int) (textX / scale), (int) (textY / scale), 0x26ff1b, true);

                matrices.pop();
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (resRegions == null) {
            super.renderBackground(context, mouseX, mouseY, 0.01f);

        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
