package dev.thoq.module.setting;

@SuppressWarnings("unused")
public final class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(final String name, final String description, final double defaultValue, final double min, final double max, final double increment) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberSetting(final String name, final String description, final double defaultValue, final double min, final double max) {
        this(name, description, defaultValue, min, max, 0.1);
    }

    @Override
    public void setValue(Double value) {
        super.setValue(Math.max(min, Math.min(max, value)));
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    public int getValueAsInt() {
        return value.intValue();
    }

    public float getValueAsFloat() {
        return value.floatValue();
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(value);
    }

    @Override
    public void setValueFromString(String value) {
        try {
            setValue(Double.parseDouble(value));
        } catch(NumberFormatException ignored) {
        }
    }


}
