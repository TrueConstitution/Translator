package kgg.translator.screen;

import kgg.translator.config.Option;
import kgg.translator.handler.ScreenOptions;
import kgg.translator.handler.WorldOptions;
import kgg.translator.handler.chathub.ChatOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class TranslateOptionScreen extends SimpleOptionsScreen {
    public TranslateOptionScreen() {
        super(null, MinecraftClient.getInstance().options, Text.literal("翻译选项"), new SimpleOption[]{
                createOption(ChatOptions.autoChat, "自动翻译聊天"),
                createOption(ChatOptions.chatTip, "聊天翻译提示"),
                createOption(ScreenOptions.autoTitle, "自动翻译标题"),
                createOption(ScreenOptions.autoScoreboard, "自动翻译计分板"),
                createOption(ScreenOptions.autoBossBar, "自动翻译boss栏"),
                createOption(ScreenOptions.autoTooltip, "自动翻译物品"),
                createOption(WorldOptions.autoEntityName, "自动翻译实体名"),
                createOption(WorldOptions.autoSign, "自动翻译告示牌"),
        });
    }

    // TODO: 2024/8/29 不包含背景
//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        ((Screen) this).render(context, mouseX, mouseY, delta);
//    }
//
//    @Override
//    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
//    }
//
//    @Override
//    public void renderBackgroundTexture(DrawContext context) {
//    }

    private static SimpleOption<Boolean> createOption(Option option, String name) {
        return SimpleOption.ofBoolean(name, option.isEnable(), option::setEnable);
    }
}
