package dev.thoq.event.events;

import dev.thoq.event.IEvent;
import net.minecraft.client.gui.ScaledResolution;

public final class Render2DEvent implements IEvent {

    private final ScaledResolution scaledResolution;

    public Render2DEvent(final ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }


}
