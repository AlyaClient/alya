package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public record SlowDownEvent() implements IEvent, ICancelable {

    private static boolean cancelled = false;

    public boolean isCanceled() {
        return cancelled;
    }

    public void cancel() {
        cancelled = true;
    }


}
