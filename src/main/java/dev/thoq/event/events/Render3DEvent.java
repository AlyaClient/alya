package dev.thoq.event.events;

import dev.thoq.event.IEvent;

//wire up
public record Render3DEvent(float partialTicks) implements IEvent {
}
