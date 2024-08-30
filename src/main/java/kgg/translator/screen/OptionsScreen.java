package kgg.translator.screen;

import kgg.translator.option.Option;
import kgg.translator.option.ScreenOption;
import kgg.translator.option.WorldOption;
import kgg.translator.option.ChatOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class OptionsScreen extends SimpleOptionsScreen {
    public OptionsScreen() {
        super(null, MinecraftClient.getInstance().options, Text.literal("翻译选项"), new SimpleOption[]{
                createOption(ChatOption.autoChat),
                createOption(ChatOption.chatTip),
                createOption(ScreenOption.autoTitle),
                createOption(ScreenOption.autoScoreboard),
                createOption(ScreenOption.autoBossBar),
                createOption(ScreenOption.autoTooltip),
                createOption(WorldOption.autoEntityName),
                createOption(WorldOption.autoSign),
        });
    }

    private static SimpleOption<Boolean> createOption(Option option) {
        return SimpleOption.ofBoolean(Text.translatable("translator.option." + option.name).getString(), option.isEnable(), option::setEnable);
    }
}
