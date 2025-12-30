package dev.thoq.event.events;

import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public class SlowDownEvent implements IEvent {

    private boolean cancelled = false;

    public SlowDownEvent() {
    }

    public boolean isCanceled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


}
