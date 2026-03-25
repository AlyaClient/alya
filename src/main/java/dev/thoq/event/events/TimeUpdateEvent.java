package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@SuppressWarnings("unused")
public class TimeUpdateEvent implements IEvent, ICancelable {
    private final S03PacketTimeUpdate s03PacketTimeUpdate;
    private boolean canceled = false;

    public TimeUpdateEvent(S03PacketTimeUpdate s03PacketTimeUpdate) {
        this.s03PacketTimeUpdate = s03PacketTimeUpdate;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    public S03PacketTimeUpdate s03PacketTimeUpdate() {
        return s03PacketTimeUpdate;
    }

    public S03PacketTimeUpdate getS03PacketTimeUpdate() {
        return s03PacketTimeUpdate;
    }
}
