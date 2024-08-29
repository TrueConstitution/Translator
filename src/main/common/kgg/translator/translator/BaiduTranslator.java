package kgg.translator.translator;

import com.google.common.hash.Hashing;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kgg.translator.command.CommandConfigurable;
import kgg.translator.exception.TranslateException;
import kgg.translator.ocr.ResRegion;
import kgg.translator.util.RequestUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public abstract class BaiduTranslator extends Translator implements CommandConfigurable {
    public static final String URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    public static final String OCR_URL = "https://fanyi-api.baidu.com/api/trans/sdk/picture";

    private String appId;
    private String appKey;
    private int delayTime = 1000;

    @Override
    public synchronized String translate(String text, String from, String to) throws IOException {
        return delay(delayTime, () -> {
            String salt = String.valueOf(System.currentTimeMillis());
            Map<String, Object> params = Map.of(
                    "q", text,
                    "from", from,
                    "to", to,
                    "appid", appId,
                    "salt", salt,
                    "sign", Hashing.md5().hashString(appId + text + salt + appKey, StandardCharsets.UTF_8).toString()
            );
            // 请求
            String result = RequestUtil.get(URL, params);
            JsonObject object = (JsonObject) JsonParser.parseString(result);
            // 解析
            JsonElement errorCode = object.get("error_code");
            if (errorCode != null) {
                throw new TranslateException(errorCode.getAsString());
            }
            StringJoiner joiner = new StringJoiner(" ");
            for (JsonElement element : object.get("trans_result").getAsJsonArray()) {
                joiner.add(element.getAsJsonObject().get("dst").getAsString());
            }

            return joiner.toString();
        });
    }

    @Override
    public ResRegion[] ocrtrans(byte[] img, String from, String to) throws IOException {
        String salt = "123";
        String imgMd5 = Hashing.md5().hashBytes(img).toString();
        String sign = Hashing.md5().hashString(appId + imgMd5 + salt + "APICUID" + "mac" + appKey, StandardCharsets.UTF_8).toString();

        Map<String, Object> params = Map.of(
                "from", from,
                "to", to,
                "appid", appId,
                "salt", salt,
                "cuid", "APICUID",
                "mac", "mac",
                "version", 3,
                "sign", sign
        );
        // 请求
        String result = RequestUtil.postFile(OCR_URL, params, img, "image");
        JsonObject object = (JsonObject) JsonParser.parseString(result);
        // 解析
        int errorCode = object.get("error_code").getAsInt();
        if (errorCode != 0) {
            throw new TranslateException(String.valueOf(errorCode));
        }

        return object.getAsJsonObject("data").getAsJsonArray("content").asList().stream().map(JsonElement::getAsJsonObject).map(c -> {
            String rect = c.get("rect").getAsString();
            // rect="1 2 3 4"
            List<Integer> list = Arrays.stream(rect.split(" ")).map(Integer::parseInt).toList();
            return new ResRegion(list.get(0), list.get(1), list.get(2), list.get(3), c.get("dst").getAsString(), c.get("src").getAsString());
        }).toArray(ResRegion[]::new);
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }


    @Override
    public String getName() {
        return "百度翻译";
    }

    public void setConfig(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
        setConfigured();
    }

    @Override
    public void read(JsonObject object) {
        setConfig(object.get("appId").getAsString(), object.get("appKey").getAsString());
        setDelayTime(object.get("delayTime").getAsInt());
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty("appId", appId);
        object.addProperty("appKey", appKey);
        object.addProperty("delayTime", delayTime);
    }
}
