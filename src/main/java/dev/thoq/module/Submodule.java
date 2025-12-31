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
