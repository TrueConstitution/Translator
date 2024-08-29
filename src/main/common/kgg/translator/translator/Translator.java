package kgg.translator.translator;

import com.google.gson.JsonObject;
import kgg.translator.command.CommandConfigurable;
import kgg.translator.exception.TranslateException;
import kgg.translator.ocr.ResRegion;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public abstract class Translator implements CommandConfigurable {
    private boolean configured = false;  // 是否已经配置

    public abstract String translate(String text, String from, String to) throws IOException;

    public abstract ResRegion[] ocrtrans(byte[] img, String from, String to) throws IOException;

    public abstract String getName();

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured() {
        setConfigured(true);
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public abstract void read(JsonObject object);
    public abstract void write(JsonObject object);

    @Override
    public String toString() {
        return getName();
    }

    private long startTime = 0;
    private final Lock lock = new ReentrantLock();
    protected <T> T delay(long time, ThrowingSupplier<T> runnable) throws IOException {
        lock.lock();
        if (System.currentTimeMillis() - startTime < time) {
            try {
                TimeUnit.MILLISECONDS.sleep(time - (System.currentTimeMillis() - startTime));
            } catch (InterruptedException ignored) {}
        }
        try {
            return runnable.get();
        } finally {
            startTime = System.currentTimeMillis();
            lock.unlock();
        }
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws IOException;
    }
}
