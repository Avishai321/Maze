package org.example;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConfigService {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public interface ConfigCallback {
        void onSuccess(RenderConfig config);
        void onError(Exception e);
    }

    public static void fetchRenderConfig(ConfigCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.CONFIG_URL))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(jsonBody -> {
                    try {
                        RenderConfig config = gson.fromJson(jsonBody, RenderConfig.class);
                        callback.onSuccess(config);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                })
                .exceptionally(e -> {
                    callback.onError(new Exception(e)); // Catch network errors
                    return null;
                });
    }
}
