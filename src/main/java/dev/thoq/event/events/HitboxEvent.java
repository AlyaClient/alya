package dev.thoq.event.events;
import dev.thoq.event.IEvent;
public final class HitboxEvent implements IEvent {
    private float expansion;
    public HitboxEvent(float expansion) {
        this.expansion = expansion;
    }
    public float getExpansion() {
        return expansion;
    }
    public void setExpansion(float expansion) {
        this.expansion = expansion;
    }
}
