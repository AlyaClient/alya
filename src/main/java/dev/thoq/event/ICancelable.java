package dev.thoq.event;
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface ICancelable {
    boolean isCanceled();
    void cancel();

}
