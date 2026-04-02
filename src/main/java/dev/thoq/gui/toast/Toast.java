package dev.thoq.gui.toast;

public final class Toast {

    public enum Type {
        INFO, SUCCESS, WARNING, DANGER
    }

    private final Type type;
    private final String title;
    private final String message;
    private final long createdAt;
    private final long durationMs;
    private float slideOffset;

    public Toast(final Type type, final String title, final String message, final long durationMs) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.durationMs = durationMs;
        this.createdAt = System.currentTimeMillis();
        this.slideOffset = 200f;
    }

    public Type getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getCreatedAt() { return createdAt; }
    public long getDurationMs() { return durationMs; }
    public float getSlideOffset() { return slideOffset; }
    public void setSlideOffset(final float offset) { this.slideOffset = offset; }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > durationMs;
    }

    public float getFadeAlpha() {
        long elapsed = System.currentTimeMillis() - createdAt;
        long fadeStart = durationMs - 500;
        if (elapsed < fadeStart) return 1f;
        float t = (elapsed - fadeStart) / 500f;
        return Math.max(0f, 1f - t);
    }


}
