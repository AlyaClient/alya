package dev.thoq.module;

import dev.thoq.Alya;
import dev.thoq.module.setting.ModeSetting;
import dev.thoq.module.setting.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
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

    public Module(final String name, final String description, final Category category, final int keyCode) {
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
        getSetting("Mode").ifPresent(setting -> {
            if(setting instanceof ModeSetting) {
                String mode = ((ModeSetting) setting).getValue();
                for(Submodule submodule : submodules) {
                    submodule.setEnabled(isEnabled() && submodule.getName().equalsIgnoreCase(mode));
                }
            }
        });
    }

    protected void addSubmodule(Submodule submodule) {
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
