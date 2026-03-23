package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class BlockPlaceableEvent implements IEvent {
  public BlockPlaceableEvent() {}

  @Override
  public boolean equals(Object o) {
    return o instanceof BlockPlaceableEvent;
  }

  @Override
  public int hashCode() {
    return BlockPlaceableEvent.class.hashCode();
  }

  @Override
  public String toString() {
    return "BlockPlaceableEvent[]";
  }
}
