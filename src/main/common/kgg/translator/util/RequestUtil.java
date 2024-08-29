package kgg.translator.util;

import com.mojang.authlib.HttpAuthenticationService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class RequestUtil {
    private static final HttpClient CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    // TODO: 2024/8/28 Url类参数

    /**
     * 用get方法请求
     */
    public static String get(String url, Map<String, Object> params) throws IOException {
        URL url1 = HttpAuthenticationService.concatenateURL(new URL(url), HttpAuthenticationService.buildQuery(params));
        HttpRequest request = HttpRequest.newBuilder(URI.create(url1.toString())).GET().build();
        return send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    /**
     * 用post方法请求，附带文件
     */
    public static String postFile(String url, Map<String, Object> params, byte[] fileData, String name) throws IOException {
        URL url1 = HttpAuthenticationService.concatenateURL(new URL(url), HttpAuthenticationService.buildQuery(params));

        // 构建请求体
        String boundary = UUID.randomUUID().toString();
        ByteArrayOutputStream bodyOutputStream = new ByteArrayOutputStream();
        bodyOutputStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        bodyOutputStream.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"file\"\r\n").getBytes(StandardCharsets.UTF_8));
        bodyOutputStream.write("Content-Type: application/octet-stream\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        bodyOutputStream.write(fileData);
        bodyOutputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        bodyOutputStream.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        // 请求

        HttpRequest request = HttpRequest.newBuilder(URI.create(url1.toString()))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(bodyOutputStream.toByteArray()))
                .build();
        return send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) throws IOException {
        try {
            return CLIENT.send(request, handler);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpClient getClient() {
        return CLIENT;
    }
}
//    public static void main(String[] args) throws Exception {
//        Path path = Path.of("C:/users/kgg/desktop/a.png");
//        byte[] bytes = Files.readAllBytes(path);
////        /*
////        #     "from": "auto",
////#     "to": "zh",
////#     "appid": app_id,
////#     "salt": "123",
////#     "cuid": "APICUID",
////#     "mac": "mac",
////#     "version": 3,
////#     "sign": sign
////         */
//        String appId = "20211003000963466";
//        String appSecret = "f0qBNq6QOO37Ep6Y8j_6";
//        HashFunction md5 = Hashing.md5();
////
//        String sign = (appId + md5.hashBytes(bytes) + "123" + "APICUID" + "mac" + appSecret);
////        System.out.println(sign);
////
////
//        String signMd5 = md5.hashString(sign, StandardCharsets.UTF_8).toString();
////        System.out.println(signMd5);
//        Map<String, Object> from = Map.of(
//                "from", "auto",
//                "to", "zh",
//                "appid", appId,
//                "salt", "123",
//                "cuid", "APICUID",
//                "mac", "mac",
//                "version", 3,
//                "sign", signMd5
//        );
////
////        System.out.println(from);
////
//        System.out.println(postFile(BaiduTranslator.OCR_URL, from, bytes, "image"));
////
////
////        // {'from': 'auto', 'to': 'zh', 'appid': '20211003000963466', 'salt': '123', 'cuid': 'APICUID', 'mac': 'mac', 'version': 3, 'sign': '15548bc077f668be6700bd6b645de980'}
////        // {from=auto, version=3, salt=123, mac=mac, sign=15548bc077f668be6700bd6b645de980, cuid=APICUID, appid=20211003000963466, to=zh}
////    }
//    }
//}
