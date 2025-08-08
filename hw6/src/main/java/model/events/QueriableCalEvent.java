package model.events;

import java.time.LocalDateTime;

/**
 * An interface representing a calendar event that can be queried about
 * all its properties.
 */
public interface QueriableCalEvent extends CalEvent {
  /**
   * Retrieve the subject of this event.
   *
   * @return the subject of this event.
   */
  String getSubject();

  /**
   * Retrieve the start date and time of this event.
   *
   * @return the start date and time of this event.
   */
  LocalDateTime getStartDateTime();

  /**
   * Retrieve the end date and time of this event.
   *
   * @return the end date and time of this event.
   */
  LocalDateTime getEndDateTime();

  /**
   * Find out whether this event is all day or not.
   *
   * @return the all-day status of this event.
   */
  boolean isAllDay();

  /**
   * Retrieve the series this event is in, if it is in one.
   *
   * @return the series this event is in, if it is in one.
   */
  RobustCalEvent.Series getSeries();

  /**
   * Retrieve the description field of this event.
   *
   * @return the description if not null, and "Description not found" if null.
   */
  String getDescription();

  /**
   * Retrieve the location of this event.
   *
   * @return the location of this event.
   */
  CalEventImpl.Location getLocation();

  /**
   * Retrieve the status of this event.
   *
   * @return the status (defaulted to private) of this event as a String.
   */
  CalEventImpl.Status getStatus();
}
