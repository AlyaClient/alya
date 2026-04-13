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

package bypass.module.setting;

public final class StringSetting extends Setting<String> {
    public StringSetting(final String name, final String description, final String defaultValue) {
        super(name, description, defaultValue);
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    public int length() {
        return value != null ? value.length() : 0;
    }

    @Override
    public String getValueAsString() {
        return value != null ? value : "";
    }

    @Override
    public void setValueFromString(final String value) {
        setValue(value);
    }
}
