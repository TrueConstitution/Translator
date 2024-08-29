package kgg.translator;

import kgg.translator.handler.OcrHandler;
import kgg.translator.screen.TranslateOptionScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    public static final KeyBinding TRANSLATE_SCREEN_KEY = new KeyBinding("key.translator.translate_screen", GLFW.GLFW_KEY_U, "key.categories.translator");
//    public static final KeyBinding TRANSLATE_SCREEN_KEY = new KeyBinding("key.translator.translate_screen", GLFW.GLFW_KEY_I, "key.categories.translator");
    public static final KeyBinding OCR_KEY = new KeyBinding("key.translator.ocr", GLFW.GLFW_KEY_O, "key.categories.translator");

    public static void register() {
        KeyBindingHelper.registerKeyBinding(TRANSLATE_SCREEN_KEY);
//        KeyBindingHelper.registerKeyBinding(TRANSLATE_WORLD_KEY);
        KeyBindingHelper.registerKeyBinding(OCR_KEY);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TRANSLATE_SCREEN_KEY.wasPressed()) {
                client.setScreen(new TranslateOptionScreen());
//                Option.setAutoScreenText(!Option.isAutoScreenText());
//                client.player.sendMessage(Text.literal("已%s屏幕翻译".formatted(Option.isAutoScreenText() ? "开启" : "关闭")));
            }
//
//            while (TRANSLATE_WORLD_KEY.wasPressed()) {
//                Option.setAutoWorldText(!Option.isAutoWorldText());
//                client.player.sendMessage(Text.literal("已%s世界翻译".formatted(Option.isAutoWorldText() ? "开启" : "关闭")));
//            }

            while (OCR_KEY.wasPressed()) {
                OcrHandler.run();
            }
        });
    }
}
