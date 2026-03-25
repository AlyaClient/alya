package dev.thoq.module.setting;

import java.util.function.Supplier;

public abstract class Setting<T> {
    private final String name;
    private final String description;
    protected T value;
    protected final T defaultValue;
    private Supplier<Boolean> visibility = () -> true;

    public Setting(final String name, final String description, final T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    private Runnable onChange;

    public void setValue(T value) {
        this.value = value;
        if(onChange != null) {
            onChange.run();
        }
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        this.value = defaultValue;
    }

    public boolean isVisible() {
        return visibility.get();
    }

    public void setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
    }

    public abstract String getValueAsString();

    public abstract void setValueFromString(final String value);
}
