package dev.thoq.module.setting;
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
