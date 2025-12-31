package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class PlayerMoveEvent implements IEvent {

    public PlayerMoveEvent() {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerMoveEvent;
    }

    @Override
    public int hashCode() {
        return PlayerMoveEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerMoveEvent[]";
    }
}
