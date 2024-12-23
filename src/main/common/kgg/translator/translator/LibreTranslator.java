package kgg.translator.translator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kgg.translator.exception.TranslateException;
import kgg.translator.util.EasyProperties;
import kgg.translator.util.RequestUtil;

import java.io.IOException;
import java.util.Map;

public abstract class LibreTranslator extends Translator {
    protected String libre_url = ""; // URL to a mirror of LibreTranslate
    protected String api_key = ""; // optional api key to such mirror
    protected int delayTime = 500;
    @Override
    public synchronized String translate(String text, String from, String to) throws IOException {
        if (libre_url.isBlank()) return text;
        return delay(delayTime, () -> {
            JsonObject params = new JsonObject();
            params.addProperty("q",  text);
            params.addProperty("source", from);
            params.addProperty("target", to);
            params.addProperty("format", "text");
            if (!api_key.isBlank()) params.addProperty("api_key", api_key);
            // 请求
            String result = RequestUtil.post(libre_url + "/translate", params);
            JsonObject object = (JsonObject) JsonParser.parseString(result);
            // 解析
            if (object.has("error")) throw new TranslateException(object.get("error").getAsString());

            return object.get("translatedText").getAsString();
        });
    }

    @Override
    public String getName() {
        return "LibreTranslate";
    }

    public void setConfig(String libre_url, String api_key) {
        if (libre_url.endsWith("/")) libre_url = libre_url.substring(libre_url.length()-1);
        this.libre_url = libre_url;
        this.api_key = api_key;
        setConfigured();
    }

    @Override
    public void read(JsonObject object) {
        setConfig(object.get("libre_url").getAsString(), object.get("api_key").getAsString());
        this.delayTime = object.get("delayTime").getAsInt();
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty("libre_url", libre_url);
        object.addProperty("api_key", api_key);
        object.addProperty("delayTime", delayTime);
    }

    public static final EasyProperties LANGUAGES;
    static {
        try {
            LANGUAGES = new EasyProperties(LibreTranslator.class.getClassLoader().getResourceAsStream("languages/libretranslate.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EasyProperties getLanguageProperties() {
        return LANGUAGES;
    }
}
