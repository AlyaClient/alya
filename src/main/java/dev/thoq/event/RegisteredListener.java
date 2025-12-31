package dev.thoq.event;

public record RegisteredListener(Class<? extends IEvent> eventClass, IEventListener<? extends IEvent> listener) {

}