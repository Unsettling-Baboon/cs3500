package model.calendars;

import java.util.Set;

import model.calendar.RobustCalendar;

/**
 * An interface representing a robust multi-calendar object that can create
 * calendars, edit them, use them, and copy events between them.
 */
public interface RobustCalendars extends
        CopiableCalendars, CreatableCalendars, EditableCalendars, UsableCalendars {
  /**
   * Retrieve the set of all the calendars currently made so far.
   *
   * @return the set of all calendars made so far.
   */
  Set<RobustCalendar> getAllCalendars();
}
