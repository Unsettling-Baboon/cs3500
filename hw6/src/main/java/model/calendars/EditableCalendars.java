package model.calendars;

import java.util.NoSuchElementException;

/**
 * An interface representing a multi-calendar object that can edit any
 * of its calendars.
 */
public interface EditableCalendars {
  /**
   * Replace the specified property of the calendar with the given name
   * with another value.
   *
   * @param name           the name of the target calendar.
   * @param property       the property to edit of the target calendar.
   * @param newPropertyVal the new value to give this property.
   * @throws NoSuchElementException if no active calendar has been set.
   */
  void editCalendar(String name, String property, String newPropertyVal)
          throws NoSuchElementException;
}
