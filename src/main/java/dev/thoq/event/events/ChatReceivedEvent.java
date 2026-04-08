package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
import net.minecraft.util.IChatComponent;

public final class ChatReceivedEvent implements IEvent, ICancelable {

    private boolean canceled = false;
    private IChatComponent message;

    public ChatReceivedEvent(final IChatComponent message) {
        this.message = message;
    }

    public IChatComponent getMessage() {
        return message;
    }

    public void setMessage(final IChatComponent message) {
        this.message = message;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }


}
