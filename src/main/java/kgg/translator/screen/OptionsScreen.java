package kgg.translator.screen;

import kgg.translator.config.Option;
import kgg.translator.config.ScreenOptions;
import kgg.translator.config.WorldOptions;
import kgg.translator.config.ChatOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class OptionsScreen extends SimpleOptionsScreen {
    public OptionsScreen() {
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

    private static SimpleOption<Boolean> createOption(Option option, String name) {
        return SimpleOption.ofBoolean(name, option.isEnable(), option::setEnable);
    }
}
