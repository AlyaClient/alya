package dev.thoq.event.events;

import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public final class MoveEntityEvent implements IEvent {

    private final double x;
    private final double y;
    private final double z;
    private boolean cancelled = false;

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
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
