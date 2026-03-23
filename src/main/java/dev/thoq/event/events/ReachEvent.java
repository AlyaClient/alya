package dev.thoq.event.events;

import dev.thoq.event.IEvent;

public final class ReachEvent implements IEvent {
  private double reachDistance;

  public ReachEvent(double reachDistance) {
    this.reachDistance = reachDistance;
  }

  public double getReachDistance() {
    return reachDistance;
  }

  public void setReachDistance(double reachDistance) {
    this.reachDistance = reachDistance;
  }
}
