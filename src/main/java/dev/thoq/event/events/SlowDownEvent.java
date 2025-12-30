package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public final class SlowDownEvent implements IEvent, ICancelable {

    private boolean cancelled = false;

    public SlowDownEvent() {
    }

    public boolean isCanceled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }


}
