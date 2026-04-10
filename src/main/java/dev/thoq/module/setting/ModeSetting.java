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

package dev.thoq.module.setting;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(
            final String name,
            final String description,
            final String defaultValue,
            final String... modes) {
        super(name, description, defaultValue);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() {
        return modes;
    }

    public boolean is(final String mode) {
        return getValue().equalsIgnoreCase(mode);
    }

    public void cycle() {
        int index = modes.indexOf(getValue());
        if(index == -1) {
            setValue(defaultValue);
            return;
        }
        index++;
        if(index >= modes.size()) {
            index = 0;
        }
        setValue(modes.get(index));
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public void setValueFromString(String value) {
        if(modes.contains(value)) {
            setValue(value);
        }
    }
}
