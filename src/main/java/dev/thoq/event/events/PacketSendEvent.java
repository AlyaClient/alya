package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
import net.minecraft.network.Packet;

public final class PacketSendEvent implements IEvent, ICancelable {

    private Packet<?> packet;
    private boolean cancled = false;

    public PacketSendEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public void setPacket(final Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    @Override
    public boolean isCanceled() {
        return this.cancled;
    }

    @Override
    public void cancel() {
        this.cancled = true;
    }


}
