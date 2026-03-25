package dev.thoq.module.setting;

public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(final String name, final String description, final boolean defaultValue) {
        super(name, description, defaultValue);
    }

    public void toggle() {
        setValue(!this.value);
    }

    public boolean isEnabled() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(value);
    }

    @Override
    public void setValueFromString(final String value) {
        setValue(Boolean.parseBoolean(value));
    }
}
