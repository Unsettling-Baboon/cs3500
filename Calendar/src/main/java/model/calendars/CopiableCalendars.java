package model.calendars;

import java.util.NoSuchElementException;

/**
 * An interface representing a multi-calendar object who can copy events
 * in between its member calendars.
 */
public interface CopiableCalendars {
  /**
   * Copy a single event from the active calendar to another.
   *
   * @param name                         the name of the event.
   * @param fromDateStringTtimeString    when this event starts.
   * @param calName                      the name of the calendar to copy over to.
   * @param newFromDateStringTtimeString when this event should start in the new
   *                                     calendar, assumed to be specified in this
   *                                     target calendar's time zone.
   * @throws NoSuchElementException if no active calendar is set.
   */
  void copySingleEvent(
          String name,
          String fromDateStringTtimeString,
          String calName,
          String newFromDateStringTtimeString
  ) throws NoSuchElementException;

  /**
   * Copy events on a certain day to another calendar, adjusting the time zones
   * of each event as needed.
   *
   * @param onDateString the date on which the target events start or end.
   * @param calName      the calendar to copy these events over to.
   * @param toDateString the date to copy these events over to, in the time zone
   *                     of the target calendar.
   * @throws NoSuchElementException if no active calendar is set.
   */
  void copyAllEventsOnDay(
          String onDateString,
          String calName,
          String toDateString
  ) throws NoSuchElementException;

  /**
   * Copy all the events in a select interval, inclusive of the endpoints.
   *
   * @param fromDateString the beginning of the interval.
   * @param toDateString   the end of the interval.
   * @param calName        the calendar to copy events over ot.
   * @param newDateString  the new date on which these events should be scheduled,
   *                       in the time zone of the target calendar.
   * @throws NoSuchElementException if no active calendar is set.
   */
  void copyAllEventsInBetweenDates(
          String fromDateString,
          String toDateString,
          String calName,
          String newDateString
  ) throws NoSuchElementException;
}
