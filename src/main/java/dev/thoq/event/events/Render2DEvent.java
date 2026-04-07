package dev.thoq.event.events;

import dev.thoq.event.IEvent;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Objects;

public record Render2DEvent(ScaledResolution scaledResolution) implements IEvent {

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Render2DEvent)) return false;
        Render2DEvent that = (Render2DEvent) o;
        return Objects.equals(scaledResolution, that.scaledResolution);
    }

    @Override
    public String toString() {
        return "Render2DEvent[scaledResolution=" + scaledResolution + "]";
    }
}
