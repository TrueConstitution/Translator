package kgg.translator.screen;

import kgg.translator.option.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class OptionsScreen extends SimpleOptionsScreen {
    public OptionsScreen() {
        super(null, MinecraftClient.getInstance().options, Text.translatable("translator.OptionsScreen.title"),
                new SimpleOption[]{
                        createOption(ChatOption.autoChat),
                        createOption(ChatOption.chatTip),
                        createOptionAndTooltip(ChatOption.translateModMessages),
                        createOptionAndTooltip(ScreenOption.autoTitle),
                        createOption(ScreenOption.autoScoreboard),
                        createOption(ScreenOption.autoBossBar),
                        createOptionAndTooltip(ScreenOption.autoTooltip),
                        createOption(ScreenOption.autoHeldItemName),
                        createOption(WorldOption.autoEntityName),
                        createOption(WorldOption.autoSign),
                        createOptionAndTooltip(ScreenOption.screenTranslate),
                        createOptionAndTooltip(WorldOption.signCombine),
                        createOptionAndTooltip(TranslateOption.useSecondaryTranslator),
                        createOptionAndTooltip(TranslateOption.splitStyledTextIntoSegments)
                });
    }

    @Override
    public void init() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            ButtonWidget translator_config_screen_button = ButtonWidget.builder(Text.translatable("translator.OptionsScreen.title"), button -> {
                MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(TranslatorScreen.buildTranslatorScreen(this)));
            }).dimensions(this.width / 2 - 100, this.height - 57, 200, 20).build();
            addDrawableChild(translator_config_screen_button);
        }
        super.init();
    }

    private static SimpleOption<Boolean> createOption(Option option) {
        return SimpleOption.ofBoolean("translator.option." + option.name, option.isEnable(), option::setEnable);
    }

    private static SimpleOption<Boolean> createOptionAndTooltip(Option option) {
        return SimpleOption.ofBoolean("translator.option." + option.name,
                (b) -> Tooltip.of(Text.translatable("translator.option." + option.name + ".desc")),
                option.isEnable(), option::setEnable);
    }
}
