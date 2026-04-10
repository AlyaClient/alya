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
package dev.thoq.util.openauth;

import dev.thoq.util.openauth.model.response.MinecraftProfile;

@SuppressWarnings("unused")
public class MicrosoftAuthResult {
    private final MinecraftProfile profile;
    private final String accessToken;
    private final String refreshToken;

    public MicrosoftAuthResult(MinecraftProfile profile, String accessToken, String refreshToken) {
        this.profile = profile;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * @return The player Minecraft profile (contains its UUID and username)
     */
    public MinecraftProfile getProfile() {
        return profile;
    }

    /**
     * @return The Minecraft access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return The Microsoft refresh token that can be used to log the user back silently using {@link
     * MicrosoftAuthenticator#loginWithRefreshToken(String)}
     */
    public String getRefreshToken() {
        return refreshToken;
    }
}
