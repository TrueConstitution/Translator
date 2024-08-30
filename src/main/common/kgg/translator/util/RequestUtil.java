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

    /**
     * 用get方法请求
     */
    public static String get(String url, Map<String, Object> params) throws IOException {
        URI uri = URI.create(HttpAuthenticationService.concatenateURL(new URL(url), HttpAuthenticationService.buildQuery(params)).toString());
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        return send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

//    public static String postForm(String url, Map<String, Object> params) throws IOException {
////        URI uri = URI.create(HttpAuthenticationService.concatenateURL(new URL(url), HttpAuthenticationService.buildQuery(params)).toString());
//
//        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
//                .header("Content-Type", "application/x-www-form-urlencoded'")
//                .POST(HttpRequest.BodyPublishers.ofString(HttpAuthenticationService.buildQuery(params)))
//                .build();
//        return send(request, HttpResponse.BodyHandlers.ofString()).body();
//    }

    /**
     * 用post方法请求，附带文件
     */
    public static String postFile(String url, Map<String, Object> params, byte[] fileData, String name) throws IOException {
        URI uri = URI.create(HttpAuthenticationService.concatenateURL(new URL(url), HttpAuthenticationService.buildQuery(params)).toString());
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
        HttpRequest request = HttpRequest.newBuilder(uri)
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