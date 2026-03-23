package dev.thoq.event.events;
import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
public final class TickEvent implements IEvent, ICancelable {
    private boolean canceled = false;
    public TickEvent() {
    }
    @Override
    public boolean equals(Object object) {
        return object instanceof TickEvent;
    }
    @Override
    public int hashCode() {
        return TickEvent.class.hashCode();
    }
    @Override
    public String toString() {
        return "TickEvent[]";
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
