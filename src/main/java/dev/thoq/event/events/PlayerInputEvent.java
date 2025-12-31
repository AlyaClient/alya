package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class PlayerInputEvent implements IEvent {

    public PlayerInputEvent() {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerInputEvent;
    }

    @Override
    public int hashCode() {
        return PlayerInputEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerInputEvent[]";
    }
}
