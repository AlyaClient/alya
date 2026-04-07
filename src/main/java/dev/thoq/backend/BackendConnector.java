package dev.thoq.backend;

import dev.thoq.Alya;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class BackendConnector {

    private static final String baseUri = "https://backend.alya.thoq.dev";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public BackendConnector() {
    }

    private void sendUserOnline() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/user/online", baseUri)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void poll() {
        try {
            sendUserOnline();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to send user online", exception);
        }
    }


}
