package dev.thoq.module.setting;
import java.util.Arrays;
import java.util.List;
@SuppressWarnings("unused")
public final class ModeSetting extends Setting<String> {
    private final List<String> modes;
    public ModeSetting(final String name, final String description, final String defaultValue, final String... modes) {
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
