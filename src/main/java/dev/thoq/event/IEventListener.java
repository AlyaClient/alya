package dev.thoq.event;
@FunctionalInterface
public interface IEventListener<T extends IEvent> {
    void onEvent(T event);
}
