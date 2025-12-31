package dev.thoq.event;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface TemporalEvent {

    boolean isPre();

    boolean isPost();

    void setPost();


}
