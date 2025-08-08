package model.calendars;

/**
 * An interface representing a multi-calendar object who can create
 * its member calendars.
 */
public interface CreatableCalendars {
  /**
   * Create a new calendar with a specified name and time zone.
   *
   * @param name the name to give this calendar.
   * @param timeZone the time zone to assign to this calendar.
   */
  void createCalendar(String name, String timeZone);
}
