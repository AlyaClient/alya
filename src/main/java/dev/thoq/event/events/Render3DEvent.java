package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class Render3DEvent implements IEvent {

    private final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }


}
