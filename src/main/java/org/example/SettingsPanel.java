package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        setPreferredSize(new Dimension(AppConfig.WIDTH, AppConfig.HEIGHT));
        setBackground(Color.DARK_GRAY);

        try (HttpClient client = HttpClient.newBuilder().build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://backend-qcf9.onrender.com/fm1/get-render-config"))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
