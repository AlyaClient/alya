package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

public final class PlayerMoveEvent implements IEvent, ICancelable {

    private boolean canceled = false;

    public PlayerMoveEvent() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PlayerMoveEvent;
    }

    @Override
    public int hashCode() {
        return PlayerMoveEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerMoveEvent[]";
    }

    @Override
    public boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }
}
