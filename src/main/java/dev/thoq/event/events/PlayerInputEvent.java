package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

public final class PlayerInputEvent implements IEvent, ICancelable {

    private boolean canceled = false;

    public PlayerInputEvent() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PlayerInputEvent;
    }

    @Override
    public int hashCode() {
        return PlayerInputEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerInputEvent[]";
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
