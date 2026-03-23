package dev.thoq.event;
import dev.thoq.Alya;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
public final class EventBus {
    private final Map<Class<? extends IEvent>, List<IEventListener<? extends IEvent>>> listeners = new ConcurrentHashMap<>();
    private final Map<Object, List<RegisteredListener>> subscriberListeners = new ConcurrentHashMap<>();
    public <T extends IEvent> void subscribe(final Class<T> eventClass, final IEventListener<T> listener) {
        listeners.computeIfAbsent(eventClass, e -> new CopyOnWriteArrayList<>()).add(listener);
    }
    public <T extends IEvent> void unsubscribe(final Class<T> eventClass, final IEventListener<T> listener) {
        final List<IEventListener<? extends IEvent>> eventListeners = listeners.get(eventClass);
        if(eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
    @SuppressWarnings("unchecked")
    public void subscribe(final Object subscriber) {
        if(subscriberListeners.containsKey(subscriber)) {
            return;
        }
        final List<RegisteredListener> registered = new ArrayList<>();
        for(final Method method : subscriber.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(EventHandler.class)) {
                continue;
            }
            if(method.getParameterCount() != 1) {
                continue;
            }
            final Class<?> paramType = method.getParameterTypes()[0];
            if(!IEvent.class.isAssignableFrom(paramType)) {
                continue;
            }
            final Class<? extends IEvent> eventClass = (Class<? extends IEvent>) paramType;
            method.setAccessible(true);
            final IEventListener<IEvent> listener = event -> {
                try {
                    method.invoke(subscriber, event);
                } catch(Exception exception) {
                    Alya.getInstance().getLogger().error("Failed to invoke event handler", exception);
                }
            };
            subscribe((Class<IEvent>) eventClass, listener);
            registered.add(new RegisteredListener(eventClass, listener));
        }
        subscriberListeners.put(subscriber, registered);
    }
    @SuppressWarnings("unchecked")
    public void unsubscribe(final Object subscriber) {
        final List<RegisteredListener> registered = subscriberListeners.remove(subscriber);
        if(registered != null) {
            for(final RegisteredListener reg : registered) {
                unsubscribe((Class<IEvent>) reg.eventClass(), (IEventListener<IEvent>) reg.listener());
            }
        }
    }
    @SuppressWarnings("unchecked")
    public <T extends IEvent> void dispatch(final T event) {
        final List<IEventListener<? extends IEvent>> eventListeners = listeners.get(event.getClass());
        if(eventListeners != null) {
            for(final IEventListener<? extends IEvent> listener : eventListeners) {
                ((IEventListener<T>) listener).onEvent(event);
            }
        }
    }

}
