package dev.thoq.event;

import java.util.Objects;

public final class RegisteredListener {

    private final Class<? extends IEvent> eventClass;
    private final IEventListener<? extends IEvent> listener;

    public RegisteredListener(Class<? extends IEvent> eventClass,
                              IEventListener<? extends IEvent> listener) {
        this.eventClass = eventClass;
        this.listener = listener;
    }

    public Class<? extends IEvent> eventClass() {
        return eventClass;
    }

    public IEventListener<? extends IEvent> listener() {
        return listener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisteredListener)) return false;
        RegisteredListener that = (RegisteredListener) o;
        return Objects.equals(eventClass, that.eventClass) &&
                Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventClass, listener);
    }

    @Override
    public String toString() {
        return "RegisteredListener[eventClass=" + eventClass + ", listener=" + listener + "]";
    }
}
