package dev.thoq.util.misc;

@SuppressWarnings("unused")
public final class TimerUtil {

    private long lastMS = System.currentTimeMillis();

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
        return System.currentTimeMillis() - lastMS > time;
    }

    public boolean hasTimeElapsed(double time) {
        return hasTimeElapsed((long) time);
    }

    public void setTime(final long time) {
        lastMS = time;
    }


}
