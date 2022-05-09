package com.ruoyi.pension.owon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class HttpClientUtil {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_READ_TIMEOUT = 50000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 50000;
    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 50000;
    private static Map<String, String> default_get_header;

    public HttpClientUtil() {
        default_get_header.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        default_get_header.put("Content-Type", "application/x-www-form-urlencoded");
    }

    /** get请求 */
    public static HttpResponse<String> get(Map<String, String> params, String requestUrl, Map<String, String> headerParams) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(5000)).build();
        if (CollectionUtils.isEmpty(headerParams)) {
            headerParams = default_get_header;
        }
        Builder builder = HttpRequest.newBuilder().uri(URI.create(requestUrl))
                .timeout(Duration.ofMillis(DEFAULT_CONNECT_TIMEOUT));
        for (Map.Entry<String, String> entry : headerParams.entrySet()) {
            builder.setHeader(entry.getKey(), entry.getValue());
        }
        HttpRequest request = builder.build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
/*        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn("http GET 请求错误", e.getMessage());
        }*/
    }

    /** post 表单请求 */
    public static HttpResponse<String> post(Map<String, String> params, String requestUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        // 使用 Stream 流编程将 [name1,value1],[name2,value2]...==变成==>
        // name1=value1&name2=value2&...
        var postFormParam = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(requestUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(postFormParam)).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());

/*        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn("http POST 表单请求错误", e.getMessage());
        }*/
    }

    /** post JOSN请求 */
    public static HttpResponse<String> post(String requestJsonParam, String requestUrl) throws ExecutionException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder(URI.create(requestUrl))
                .timeout(Duration.ofMillis(DEFAULT_READ_TIMEOUT)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJsonParam)).build();

        CompletableFuture<HttpResponse<String>> result = HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return result.get();

/*        CompletableFuture<String> result = HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("http POST json请求错误", e.getMessage());
        }*/
    }
    public static String postOfBody(String requestJsonParam, String requestUrl) throws ExecutionException, InterruptedException {
        return post(requestJsonParam,requestUrl).body();
    }


    /** post 文件上传 *
    public static void post(File file, String fileParamKey, String requestUrl) {
        HttpClient client = HttpClient.newHttpClient();

        // Could be any string
        String boundary = "N5xJwtEHgQBVKxPs_uedAVv1Kjasjfzd5cwOR_";

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(requestUrl))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofInputStream(()->fileInputStream.nullInputStream())).build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (FileNotFoundException e) {
            log.warn("文件找不到", file.getPath());
        }catch (IOException | InterruptedException e) {
            log.warn("http POST 文件请求错误", file.getPath());
        }

    }

    /** get 文件下载 */
    public static void download(String filePath ,String requestUrl){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();

        CompletableFuture<Path> result = client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(Paths.get(filePath)))
                .thenApply(HttpResponse::body);
        try {
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("文件下载失败", filePath);
        }
    }

    /** 并发请求 */
    public static void concurrencyRequest(String... requestUrl){
        HttpClient client = HttpClient.newHttpClient();
        List<String> urls = List.of(requestUrl);
        List<HttpRequest> requests = urls.stream()
                .map(url -> HttpRequest.newBuilder(URI.create(url)))
                .map(reqBuilder -> reqBuilder.build())
                .collect(Collectors.toList());
        List<CompletableFuture<HttpResponse<String>>> futures = requests.stream()
                .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .collect(Collectors.toList());
        futures.stream().forEach(e -> e.whenComplete((resp,err) -> {
            if(err != null){
                log.warn("并发请求失败");
            }else{
                var body = resp.body();
                var statusCode = resp.statusCode();
            }
        }));
        CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new)).join();
    }

    /** websocket 发送 */
    public static void sendWebSocket(String text,String webSocketUrl){
        HttpClient client = HttpClient.newHttpClient();
        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(webSocketUrl), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        webSocket.request(1);
                        return CompletableFuture.completedFuture(data).thenAccept(System.out::println);
                    }
                }).join();
        webSocket.sendText(text, false);

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok").join();
    }


    public static <T> T postGetBody(String requestJsonParam, String requestUrl) throws ExecutionException, InterruptedException, JsonProcessingException {
        HttpResponse<String> response = post(requestJsonParam,requestUrl);
        return result(response.body(), new TypeReference<T>(){});
    }
    private static <R> R result(String result,TypeReference<R> typeReference) throws JsonProcessingException {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .build()
                .readValue(result,typeReference);
    }

}
