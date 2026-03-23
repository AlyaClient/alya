package dev.thoq.event.events;
import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
@SuppressWarnings("unused")
public final class SlowDownEvent implements IEvent, ICancelable {
    private final String reason;
    private boolean cancelled = false;
    public SlowDownEvent(String reason) {
        this.reason = reason;
    }
    public String getReason() {
        return reason;
    }
    @Override
    public boolean isCanceled() {
        return cancelled;
    }
    @Override
    public void cancel() {
        cancelled = true;
    }
}
