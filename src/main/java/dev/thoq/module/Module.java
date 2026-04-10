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
import dev.thoq.module.setting.ModeSetting;
import dev.thoq.module.setting.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class Module {
    protected static final Minecraft MC = Minecraft.getMinecraft();
    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings;
    private final List<Submodule> submodules;
    private boolean enabled;
    private int keyCode;

    public Module(final String name, final String description, final Category category) {
        this(name, description, category, Keyboard.KEY_NONE);
    }

    public Module(
            final String name, final String description, final Category category, final int keyCode) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keyCode = keyCode;
        this.settings = new ArrayList<>();
        this.submodules = new ArrayList<>();
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if(this.enabled != enabled) {
            this.enabled = enabled;
            if(enabled) {
                Alya.getInstance().getEventBus().subscribe(this);
                onEnable();
                updateSubmodules();
            } else {
                onDisable();
                for(Submodule submodule : submodules) {
                    submodule.setEnabled(false);
                }
                Alya.getInstance().getEventBus().unsubscribe(this);
            }
        }
    }

    protected void updateSubmodules() {
        getSetting("Mode")
                .ifPresent(
                        setting -> {
                            if(setting instanceof ModeSetting) {
                                String mode = ((ModeSetting) setting).getValue();
                                for(Submodule submodule : submodules) {
                                    submodule.setEnabled(isEnabled() && submodule.getName().equalsIgnoreCase(mode));
                                }
                            }
                        });
    }

    protected void initializeSubmodules(final Submodule... submodules) {
        for(final Submodule submodule : submodules) {
            addSubmodule(submodule);
        }
    }

    private void addSubmodule(final Submodule submodule) {
        submodules.add(submodule);
    }

    public List<Submodule> getSubmodules() {
        return submodules;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(final int keyCode) {
        this.keyCode = keyCode;
    }

    @SafeVarargs
    protected final <T extends Setting<?>> void initializeSettings(final T... theSettings) {
        Collections.addAll(settings, theSettings);
    }

    private <T extends Setting<?>> void addSetting(final T setting) {
        settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public Optional<Setting<?>> getSetting(final String name) {
        return settings.stream().filter(string -> string.getName().equalsIgnoreCase(name)).findFirst();
    }

    public boolean hasSettings() {
        return !settings.isEmpty();
    }
}
