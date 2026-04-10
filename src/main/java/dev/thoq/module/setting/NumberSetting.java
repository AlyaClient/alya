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

package dev.thoq.module.setting;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public final class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double increment;
    private boolean rangeEnabled;
    private double secondValue;

    public NumberSetting(
            final String name,
            final String description,
            final double defaultValue,
            final double min,
            final double max,
            final double increment) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.rangeEnabled = false;
        this.secondValue = defaultValue;
    }

    public NumberSetting(
            final String name,
            final String description,
            final double defaultValue,
            final double min,
            final double max) {
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

    public boolean isRangeEnabled() {
        return rangeEnabled;
    }

    public void setRangeEnabled(boolean rangeEnabled) {
        this.rangeEnabled = rangeEnabled;
    }

    public double getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(double secondValue) {
        this.secondValue = Math.max(min, Math.min(max, secondValue));
    }

    public int getSecondValueAsInt() {
        return (int) secondValue;
    }

    public double getRandomValue() {
        if(!rangeEnabled) return value;
        double lo = Math.min(value, secondValue);
        double hi = Math.max(value, secondValue);
        if(lo >= hi) return lo;
        return ThreadLocalRandom.current().nextDouble(lo, hi);
    }

    public int getRandomValueAsInt() {
        if(!rangeEnabled) return value.intValue();
        int lo = (int) Math.min(value, secondValue);
        int hi = (int) Math.max(value, secondValue);
        if(lo >= hi) return lo;
        return ThreadLocalRandom.current().nextInt(lo, hi + 1);
    }

    public int getValueAsInt() {
        return value.intValue();
    }

    public float getValueAsFloat() {
        return value.floatValue();
    }

    @Override
    public String getValueAsString() {
        if(rangeEnabled) {
            return value + "-" + secondValue;
        }
        return String.valueOf(value);
    }

    @Override
    public void setValueFromString(String value) {
        try {
            if(rangeEnabled && value.contains("-")) {
                String[] parts = value.split("-", 2);
                setValue(Double.parseDouble(parts[0]));
                setSecondValue(Double.parseDouble(parts[1]));
            } else {
                setValue(Double.parseDouble(value));
            }
        } catch(NumberFormatException ignored) {
        }
    }
}
