package dev.thoq.gui.toast;

public final class Toast {

    public enum Type {
        INFO, SUCCESS, WARNING, DANGER
    }

    private static final long SLIDE_IN_MS = 250;
    private static final long SLIDE_OUT_MS = 250;

    private final Type type;
    private final String title;
    private final String message;
    private final long createdAt;
    private final long durationMs;

    public Toast(final Type type, final String title, final String message, final long durationMs) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.durationMs = durationMs;
        this.createdAt = System.currentTimeMillis();
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public long getDurationMs() {
        return durationMs;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > durationMs;
    }

    public float getSlide() {
        final long elapsed = System.currentTimeMillis() - createdAt;
        if(elapsed < SLIDE_IN_MS) {
            float time = elapsed / (float) SLIDE_IN_MS;
            return time * (2f - time);
        }
        final long remaining = durationMs - elapsed;
        if(remaining < SLIDE_OUT_MS) {
            float t = remaining / (float) SLIDE_OUT_MS;
            return t * (2f - t);
        }
        return 1.0f;
    }

    public float getProgress() {
        long elapsed = System.currentTimeMillis() - createdAt;
        return 1f - Math.clamp(elapsed / (float) durationMs, 0f, 1f);
    }


}
