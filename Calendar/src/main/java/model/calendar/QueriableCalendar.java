package model.calendar;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import model.events.RobustCalEvent;

/**
 * An interface representing all the things that a {@link QueriableCalendar}
 * object should be able to offer its users with respect to finding out
 * relevant information about events it already possesses.
 */
public interface QueriableCalendar {
  /**
   * Get the name of this calendar.
   *
   * @return the name of this calendar.
   */
  String getName();

  /**
   * Get the time zone of this calendar as a String.
   *
   * @return the time zone of this calendar as a String.
   */
  String getZone();

  /**
   * Retrieve all the events in this calendar.
   *
   * @return the set of all events in this calendar.
   */
  Set<RobustCalEvent> getEvents();

  /**
   * Print a bulleted list of all events on a specified day, along with
   * their start and end time, as well as their location (if any).
   *
   * @param dateString the date in question.
   * @return a list of all the events satisfying the conditions.
   */
  List<RobustCalEvent> printAllEventsOnDate(String dateString);

  /**
   * Print a bulleted list of all events within a given interval, along with
   * their start and end times, as well as their locations (if any).
   *
   * @param fromDateStringTtimeString the beginning of the interval.
   * @param toDateStringTtimeString   the end of the interval.
   * @return a list of all events satisfying the conditions.
   */
  List<RobustCalEvent> printAllEventsBetweenDates(
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  );

  /**
   * Show the user's status at a given date and time, which is busy if the
   * given time falls within the duration of an event, and available if the
   * given time does not fall in the duration of any event.
   *
   * @param dateStringTtimeString the date and time in question of availability.
   * @return the status as a String: "busy" or "available".
   */
  String showStatusOnDateAtTime(String dateStringTtimeString);
}
