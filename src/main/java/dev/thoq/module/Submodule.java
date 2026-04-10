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

package dev.thoq.module;

import dev.thoq.Alya;
import dev.thoq.module.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Submodule {
    protected static final Minecraft MC = Minecraft.getMinecraft();
    private final String name;
    protected final Module parent;
    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;

    public Submodule(final String name, final Module parent) {
        this.name = name;
        this.parent = parent;
    }

    public void setEnabled(final boolean enabled) {
        if(this.enabled != enabled) {
            this.enabled = enabled;
            if(enabled) {
                onEnable();
                Alya.getInstance().getEventBus().subscribe(this);
            } else {
                onDisable();
                Alya.getInstance().getEventBus().unsubscribe(this);
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public String getName() {
        return name;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    @SafeVarargs
    protected final <T extends Setting<?>> void initializeSettings(final T... theSettings) {
        Collections.addAll(settings, theSettings);
        parent.initializeSettings(theSettings);
    }
}
