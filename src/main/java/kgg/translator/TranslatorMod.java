package kgg.translator;

import kgg.translator.command.TranslateCommand;
import kgg.translator.command.TranslateConfigCommand;
import kgg.translator.handler.KeyBindingHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class TranslatorMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            TranslateCommand.register(dispatcher);
            TranslateConfigCommand.register(dispatcher);
        });

        Initializer.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            TranslatorConfig.readFile();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            TranslatorConfig.writeFile();
        });

        KeyBindingHandler.register();
    }
}
