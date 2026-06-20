package org.example.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.config.AppConfig;
import org.example.config.RenderConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ConfigService {
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public enum ErrorType {
        NETWORK,
        SERVER,
        PARSE,
        UNKNOWN
    }

    public interface ConfigCallback {
        void onSuccess(RenderConfig config);
        void onError(ErrorType type);
    }

    public static void fetchRenderConfig(ConfigCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.CONFIG_URL))
                .GET()
                .timeout(Duration.ofSeconds(15))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();

                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            RenderConfig config = gson.fromJson(response.body(), RenderConfig.class);
                            callback.onSuccess(config);
                        } catch (JsonSyntaxException e) {
                            callback.onError(ErrorType.PARSE);
                        } catch (Exception e) {
                            callback.onError(ErrorType.UNKNOWN);
                        }
                    } else {
                        callback.onError(ErrorType.SERVER);
                    }
                })
                .exceptionally(e -> {
                    callback.onError(ErrorType.NETWORK);
                    return null;
                });
    }
}
