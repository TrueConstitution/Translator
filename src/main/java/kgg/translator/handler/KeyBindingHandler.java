package kgg.translator.handler;

import kgg.translator.screen.OptionsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    public static final KeyBinding TRANSLATE_SCREEN_KEY = new KeyBinding("key.translator.options", GLFW.GLFW_KEY_U, "key.categories.translator");
    public static final KeyBinding OCR_KEY = new KeyBinding("key.translator.ocr", GLFW.GLFW_KEY_O, "key.categories.translator");

    public static void register() {
        KeyBindingHelper.registerKeyBinding(TRANSLATE_SCREEN_KEY);
        KeyBindingHelper.registerKeyBinding(OCR_KEY);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TRANSLATE_SCREEN_KEY.wasPressed()) {
                client.setScreen(new OptionsScreen());
            }

            while (OCR_KEY.wasPressed()) {
                OcrHandler.start();
            }
        });
    }
}
