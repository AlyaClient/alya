package dev.thoq.module;

import dev.thoq.Alya;
import dev.thoq.module.setting.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Module {
    protected static final Minecraft MC = Minecraft.getMinecraft();

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings;
    private boolean enabled;
    private int keyCode;

    public Module(final String name, final String description, final Category category) {
        this(name, description, category, Keyboard.KEY_NONE);
    }

    public Module(final String name, final String description, final Category category, final int keyCode) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keyCode = keyCode;
        this.settings = new ArrayList<>();
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
            } else {
                onDisable();
                Alya.getInstance().getEventBus().unsubscribe(this);
            }
        }
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

    protected <T extends Setting<?>> void addSetting(T setting) {
        settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public Optional<Setting<?>> getSetting(final String name) {
        return settings.stream()
                .filter(string -> string.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean hasSettings() {
        return !settings.isEmpty();
    }
}
