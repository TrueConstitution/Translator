package kgg.translator.translator;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kgg.translator.exception.TranslateException;
import kgg.translator.util.RequestUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class YouDaoTranslator extends Translator {
    public static final String URL = "https://openapi.youdao.com/api";
    private static final String OCR_URL = "https://openapi.youdao.com/ocrtransapi";
    private String appKey;
    private String appSecret;

    @Override
    public synchronized String translate(String text, String from, String to) throws IOException {
        return delay(800, () -> {
            Map<String, Object> params = buildParams(text, from, to);
            params.put("strict", "true");
            // 请求
            String result = RequestUtil.get(URL, params);
            JsonObject object = (JsonObject) JsonParser.parseString(result);
            // 解析
            String errorCode = object.get("errorCode").getAsString();
            if (!errorCode.equals("0")) {
                throw new TranslateException(errorCode);
            }
            return object.get("translation").getAsString();
        });
    }

    private Map<String, Object> buildParams(String q, String from, String to) {
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        String input = q.length() > 20 ? q.substring(0, 10) + q.length() + q.substring(q.length() - 10) : q;
        String sign = Hashing.sha256().hashString(appKey + input + curTime + curTime + appSecret, StandardCharsets.UTF_8).toString();
        HashMap<String, Object> params = new HashMap<>();
        params.put("q", q);
        params.put("from", from);
        params.put("to", to);
        params.put("appKey", appKey);
        params.put("salt", curTime);
        params.put("sign", sign);
        params.put("signType", "v3");
        params.put("curtime", curTime);
        return params;
    }


    public void setConfig(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        setConfigured();
    }

    @Override
    public String getName() {
        return "有道翻译";
    }

    @Override
    public void read(JsonObject object) {
        setConfig(object.get("appKey").getAsString(), object.get("appSecret").getAsString());
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty("appKey", appKey);
        object.addProperty("appSecret", appSecret);
    }
}
