package model.calendar;

/**
 * An interface representing a robust calendar that offers not only the option
 * to create events but also to edit them, as well as the option to query
 * this calendar, and retrieve or edit the time zone and name of this calendar.
 */
public interface RobustCalendar extends
        EventCreatableCalendar, EventEditableCalendar, QueriableCalendar {
  /**
   * Set a new name for this calendar.
   *
   * @param name the new name to give this calendar.
   */
  void setName(String name);

  /**
   * Set a new time zone for this calendar.
   *
   * @param zone the new time zone to assign to this calendar.
   */
  void setZone(String zone);
}
