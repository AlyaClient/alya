package dev.thoq.util.misc;

public final class TimerUtil {

    private long lastMS = System.currentTimeMillis();
    private final long lastNS = System.nanoTime();

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(final long time, final boolean reset) {
        if(System.currentTimeMillis() - lastMS > time) {
            if(reset) reset();
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public boolean hasTimeElapsed(long time) {
        return System.nanoTime() - lastNS > time * 1_000_000;
    }

    public boolean hasTimeElapsed(double time) {
        return hasTimeElapsed((long) time);
    }

    public void setTime(final long time) {
        lastMS = time;
    }


}
