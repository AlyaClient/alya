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

package dev.thoq.lua;

import java.util.ArrayList;
import java.util.List;

public final class ScriptsUtil {

    private final List<String> rawScripts;

    public ScriptsUtil() {
        this.rawScripts = new ArrayList<>();
    }

    public String[] putAll(final Script ...scripts) {
        for(final Script script : scripts) {
            this.rawScripts.add(String.format("modules/%s/%s.lua", script.category().toString().toLowerCase(), script.name()));
        }
        return this.rawScripts.toArray(new String[0]);
    }


}
