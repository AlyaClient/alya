package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public final class SlowDownEvent implements IEvent, ICancelable {


    private final String reason;
    private boolean cancelled = false;

    public SlowDownEvent(final String reason) {
        this.reason = normalizeReason(reason);
    }

    private static String normalizeReason(final String raw) {
        if(raw == null) {
            return "unknown";
        }
        return switch(raw.toLowerCase()) {
            case "eat" -> "eat";
            case "drink" -> "drink";
            case "block" -> "block";
            case "bow" -> "bow";
            default -> "unknown";
        };
    }

    public String getType() {
        return "slowdown";
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
