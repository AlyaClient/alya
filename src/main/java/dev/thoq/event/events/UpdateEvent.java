package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class UpdateEvent implements IEvent {
    public UpdateEvent() {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UpdateEvent;
    }

    @Override
    public int hashCode() {
        return UpdateEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "UpdateEvent[]";
    }
}
