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

package start;

import dev.thoq.util.misc.Array;
import dev.thoq.util.misc.Platform;

public final class Main {

    static void main(final String[] args) {
        Platform.apply();
        final String[] combined = Array.concat(new String[]{
                "--version", "1.8.9",
                "--accessToken", "0",
                "--assetsDir", "assets",
                "--userProperties", "{}"
        }, args);
        net.minecraft.client.main.Main.main(combined);
    }


}
