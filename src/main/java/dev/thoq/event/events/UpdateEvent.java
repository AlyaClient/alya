package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

public final class UpdateEvent implements IEvent, ICancelable {

    private boolean canceled = false;

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

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }
}
