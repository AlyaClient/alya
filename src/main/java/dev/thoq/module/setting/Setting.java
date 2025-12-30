package dev.thoq.module.setting;

public abstract class Setting<T> {
    
    private final String name;
    private final String description;
    protected T value;
    protected final T defaultValue;
    
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
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getDefaultValue() {
        return defaultValue;
    }
    
    public void reset() {
        this.value = defaultValue;
    }
    
    public abstract String getValueAsString();
    
    public abstract void setValueFromString(final String value);


}
