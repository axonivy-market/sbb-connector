package com.axonivy.connector.sbb.demo.trip;

public enum SegmentIconClass {
  BOAT("fa-ship"),
  BUS("fa-bus"),
  CABLE("fa-cable-car"),
  METRO("fa-train-subway"),
  TRAIN("fa-train"),
  TRAMWAY("fa-train-tram"),
  CHAIRLIFT("fa-person-ski-lift"),
  COG_RAILWAY("fa-roller-coaster"),
  TAXI("fa-taxi"),
  LIFT("fa-elevator"),
  AIRPLANE("fa-plane"),
  WALK("fa-person-walking"),
  UNKNOWN("fa-route");

  public final String iconClass;

  private SegmentIconClass(String iconclass) {
    this.iconClass = iconclass;
  }
}
