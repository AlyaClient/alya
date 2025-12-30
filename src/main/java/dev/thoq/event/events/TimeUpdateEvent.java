package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@SuppressWarnings("unused")
public final class TimeUpdateEvent implements IEvent, ICancelable {

    private boolean canceled = false;
    private final S03PacketTimeUpdate s03PacketTimeUpdate;

    public TimeUpdateEvent(final S03PacketTimeUpdate s03PacketTimeUpdate) {
        this.s03PacketTimeUpdate = s03PacketTimeUpdate;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }

    public S03PacketTimeUpdate getS03PacketTimeUpdate() {
        return s03PacketTimeUpdate;
    }


}
