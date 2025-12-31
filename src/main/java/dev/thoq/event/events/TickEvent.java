package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class TickEvent implements IEvent {

    public TickEvent() {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TickEvent;
    }

    @Override
    public int hashCode() {
        return TickEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "TickEvent[]";
    }
}
