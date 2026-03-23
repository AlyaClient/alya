package dev.thoq.event.events;
import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
@SuppressWarnings("unused")
public final class MoveEntityEvent implements IEvent, ICancelable {
    private final double x;
    private final double y;
    private final double z;
    private boolean canceled = false;
    public MoveEntityEvent(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public boolean isCanceled() {
        return canceled;
    }
    public void cancel() {
        this.canceled = true;
    }
}
