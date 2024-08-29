package kgg.translator.handler;

import kgg.translator.TranslatorManager;
import kgg.translator.ocr.ResRegion;
import kgg.translator.screen.OcrScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OcrHandler {
    // log
    private static final Logger LOGGER = LogManager.getLogger(OcrHandler.class);

    public static void run() {
        MinecraftClient client = MinecraftClient.getInstance();
        OcrScreen screen = new OcrScreen(client.currentScreen);
        client.setScreen(screen);

        try (NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer())){
            byte[] bytes = nativeImage.getBytes();
            CompletableFuture.runAsync(() -> {
                try {
                    ResRegion[] ocrtrans = TranslatorManager.ocrtrans(bytes);
                    // 每个区域进行缩放
                    ocrtrans = Arrays.stream(ocrtrans).map(resRegion -> {
                        return resRegion.scale(1 / client.getWindow().getScaleFactor());
                    }).toArray(ResRegion[]::new);
                    screen.setResRegions(ocrtrans);
                } catch (Exception e) {
                    screen.setResRegions(new ResRegion[0]);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
