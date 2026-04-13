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
package bypass.util.openauth.model.response;

@SuppressWarnings("unused")
public class MinecraftStoreResponse {
    private final StoreProduct[] items;
    private final String signature;
    private final String keyId;

    public MinecraftStoreResponse(StoreProduct[] items, String signature, String keyId) {
        this.items = items;
        this.signature = signature;
        this.keyId = keyId;
    }

    public StoreProduct[] getItems() {
        return items;
    }

    public String getSignature() {
        return signature;
    }

    public String getKeyId() {
        return keyId;
    }

    public static class StoreProduct {
        private final String name;
        private final String signature;

        public StoreProduct(String name, String signature) {
            this.name = name;
            this.signature = signature;
        }

        public String getName() {
            return name;
        }

        public String getSignature() {
            return signature;
        }
    }
}
