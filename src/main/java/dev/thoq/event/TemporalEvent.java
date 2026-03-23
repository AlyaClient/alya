package dev.thoq.event;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unused"})
public interface TemporalEvent {
  boolean isPre();

  boolean isPost();

  void setPost();
}
