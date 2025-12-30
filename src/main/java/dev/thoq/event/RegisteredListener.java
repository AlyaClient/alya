package dev.thoq.event;

public class RegisteredListener {
    final Class<? extends IEvent> eventClass;
    final IEventListener<? extends IEvent> listener;

    RegisteredListener(final Class<? extends IEvent> eventClass, final IEventListener<? extends IEvent> listener) {
        this.eventClass = eventClass;
        this.listener = listener;
    }
}