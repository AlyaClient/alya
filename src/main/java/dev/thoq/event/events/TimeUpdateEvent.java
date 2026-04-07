package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@SuppressWarnings("unused")
public class TimeUpdateEvent implements IEvent {

    private long time;

    public TimeUpdateEvent(final long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(final long time) {
        this.time = time;
    }


}
