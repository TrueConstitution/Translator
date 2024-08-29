package kgg.translator.translator;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kgg.translator.exception.TranslateException;
import kgg.translator.ocr.ResRegion;
import kgg.translator.util.RequestUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class YouDaoTranslator extends Translator {
    public static final String URL = "https://openapi.youdao.com/api";
    private static final String OCR_URL = "https://openapi.youdao.com/ocrtransapi";
    private String appKey;
    private String appSecret;

    @Override
    public synchronized String translate(String text, String from, String to) throws IOException {
        return delay(800, () -> {
            String curTime = String.valueOf(System.currentTimeMillis() / 1000);
            String input = text.length() > 20 ? text.substring(0, 10) + text.length() + text.substring(text.length() - 10) : text;
            String sign = Hashing.sha256().hashString(appKey + input + curTime + curTime + appSecret, StandardCharsets.UTF_8).toString();
            Map<String, Object> params = Map.of(
                    "q", text,
                    "from", from,
                    "to", to,
                    "appKey", appKey,
                    "salt", curTime,
                    "sign", sign,
                    "signType", "v3",
                    "curtime", curTime,
                    "strict", "true"
            );
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

    // TODO: 2024/8/28 有道
    @Override
    public ResRegion[] ocrtrans(byte[] img, String from, String to) throws TranslateException {
        return new ResRegion[0];
//        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
//        // 其中，input的计算方式为：input=q前10个字符 + q长度 + q后10个字符（当q长度大于20）或 input=q字符串（当q长度小于等于20）；
//        String input = base64.length() > 20 ? base64.substring(0, 10) + base64.length() + base64.substring(base64.length() - 10) : base64;
//        // sign=sha256(应用ID+input+salt+curtime+应用密钥)；
//        String sign = Hashing.sha256().hashString(appKey + input + curTime + curTime + appSecret, StandardCharsets.UTF_8).toString();
//        Map<String, Object> params = Map.of(
//                "type", "1",
//                "from", from,
//                "to", to,
//                "appKey", appKey,
//                "salt", curTime,
//                "sign", sign,
//                "signType", "v3",
//                "curtime", curTime,
//                "q", base64
//        );
//
//        try {
//            String result = RequestUtil.get(OCR_URL, params);
//            JsonObject object = (JsonObject) JsonParser.parseString(result);
//            // 解析
//            String errorCode = object.get("errorCode").getAsString();
//            if (!errorCode.equals("0")) {
//                throw new TranslateException(errorCode);
//            }
//
//            JsonArray resRegions = object.getAsJsonArray("resRegions");
//            // 处理后转为ResRegions[]
//            return resRegions.asList().stream().map(element -> {
//                JsonObject resRegion = element.getAsJsonObject();
//                JsonArray box = resRegion.getAsJsonArray("boundingBox");
//                return new ResRegion(box.get(0).getAsInt(), box.get(1).getAsInt(), box.get(2).getAsInt(), box.get(3).getAsInt(), resRegion.get("tranContent").getAsString());
//            }).toArray(ResRegion[]::new);
//
//        } catch (Exception e) {
//            if (e instanceof TranslateException c) {
//                throw c;
//            } else {
//                throw new TranslateException(e);
//            }
//        }
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

//    public static void main(String[] args) throws TranslateException {
//        YouDaoTranslator translator = new YouDaoTranslatorImpl();
//        translator.setAppKey("4aa7a0baa68e33ee", "TQ9IjHMuLGfDhN5COELwX83Lrv1Nuppt");
//        System.out.println(translator.translate("Hello", "auto", "zh-CHS"));
//    }
}
