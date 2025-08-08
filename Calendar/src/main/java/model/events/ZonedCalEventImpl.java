package model.events;

import java.time.ZoneId;

/**
 * An auxiliary class that stores the zone of a calendar in an event
 * for printing purposes, never used other than the case of methods
 * that print events.
 */
public class ZonedCalEventImpl extends CalEventImpl {
  private final ZoneId zone;

  public ZonedCalEventImpl(RobustCalEvent c, ZoneId zone) {
    this.subject = c.getSubject();
    this.startDateTime = c.getStartDateTime();
    this.endDateTime = c.getEndDateTime();
    this.isAllDay = c.isAllDay();
    this.series = c.getSeries();
    this.location = c.getLocation();
    this.zone = zone;
  }

  @Override
  public String toString() {
    return String.format("%s in time zone %s", super.toString(), zone);
  }
}
