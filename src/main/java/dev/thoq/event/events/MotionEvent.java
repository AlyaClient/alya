package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;
import dev.thoq.event.TemporalEvent;

public final class MotionEvent implements IEvent, ICancelable, TemporalEvent {
  private double x;
  private double y;
  private double z;
  private float yaw;
  private float pitch;
  private boolean onGround;
  private boolean canceled = false;
  private boolean pre = true;

  public MotionEvent(
      final double x,
      final double y,
      final double z,
      final float yaw,
      final float pitch,
      final boolean onGround) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
    this.onGround = onGround;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public float getYaw() {
    return yaw;
  }

  public float getPitch() {
    return pitch;
  }

  public boolean isOnGround() {
    return onGround;
  }

  public void setX(final double x) {
    this.x = x;
  }

  public void setY(final double y) {
    this.y = y;
  }

  public void setZ(final double z) {
    this.z = z;
  }

  public void setYaw(final float yaw) {
    this.yaw = yaw;
  }

  public void setPitch(final float pitch) {
    this.pitch = pitch;
  }

  public void setOnGround(final boolean onGround) {
    this.onGround = onGround;
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void cancel() {
    this.canceled = true;
  }

  @Override
  public boolean isPre() {
    return pre;
  }

  @Override
  public boolean isPost() {
    return !pre;
  }

  @Override
  public void setPost() {
    this.pre = false;
  }
}
