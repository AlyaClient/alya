package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public final class SlowDownEvent implements IEvent, ICancelable {

    private static boolean cancelled = false;

    public SlowDownEvent() {
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SlowDownEvent;
    }

    @Override
    public int hashCode() {
        return SlowDownEvent.class.hashCode();
    }

    @Override
    public String toString() {
        return "SlowDownEvent[]";
    }
}
