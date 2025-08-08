package model.calendars;

import java.util.NoSuchElementException;

import model.calendar.RobustCalendar;

/**
 * An interface representing a multi-calendar object who can set its active
 * calendar and retrieve it.
 */
public interface UsableCalendars {
  /**
   * Set the active calendar.
   *
   * @param name the name of the target calendar.
   * @throws NoSuchElementException if no calendars exist yet.
   */
  void useCalendar(String name) throws NoSuchElementException;

  /**
   * Get the currently active calendar.
   *
   * @return the active calendar
   * @throws NoSuchElementException if no active calendar was set.
   */
  RobustCalendar getActiveCal() throws NoSuchElementException;
}
