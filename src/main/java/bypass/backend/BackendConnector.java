/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package bypass.backend;

import bypass.Alya;
import net.minecraft.client.Minecraft;

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
        if(!Minecraft.getMinecraft().gameSettings.sense) return;
        try {
            sendUserOnline();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to send user online", exception);
        }
    }


}
