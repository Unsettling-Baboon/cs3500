package view;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Defines the interface for a calendar view.
 * The view is responsible for displaying data and forwarding user actions to a listener.
 * It does not contain any application logic.
 */
public interface CalendarView {
  /**
   * Registers a listener for all user actions originating from the view.
   *
   * @param listener the ActionListener (typically the controller) to handle events.
   */
  void addActionListener(ActionListener listener);

  /**
   * Renders the main calendar grid for a given month.
   *
   * @param monthToDisplay the month and year to display.
   * @param calendarColors a map of calendar names to their display colors.
   * @param activeCalendar the name of the currently active calendar.
   * @param eventsOfMonth  a map where keys are dates and values are lists of event titles on that day.
   */
  void displayMonth(YearMonth monthToDisplay, Map<String, Color> calendarColors,
                    String activeCalendar, Map<LocalDate, List<String>> eventsOfMonth);

  /**
   * Updates the event details panel to show information for a specific day.
   *
   * @param date        the date for which to show events.
   * @param eventTitles a list of event titles for the given date.
   */
  void updateEventDetails(LocalDate date, List<String> eventTitles);

  /**
   * Updates the list of available calendars in the dropdown menu.
   *
   * @param calendarNames a set of all calendar names.
   * @param selected      the name of the calendar to be selected by default.
   */
  void updateCalendarDropdown(Set<String> calendarNames, String selected);

  /**
   * Gets the text from the new event input field.
   *
   * @return the user-entered event description.
   */
  String getNewEventText();

  /**
   * Makes the main application window visible.
   */
  void makeVisible();

  /**
   * Clears all input fields in the event creation form.
   */
  void clearEventForm();

  /**
   * Checks if the all-day checkbox is selected.
   *
   * @return true if the event should be all-day, false otherwise.
   */
  boolean isAllDayEvent();

  /**
   * Checks if the recurring event option is selected.
   *
   * @return true if the event should be recurring, false otherwise.
   */
  boolean isRecurringEvent();

  /**
   * Gets the selected start time from the time dropdown.
   *
   * @return the start time as a string in HH:mm format, or null if not selected.
   */
  String getStartTime();

  /**
   * Gets the selected end time from the time dropdown.
   *
   * @return the end time as a string in HH:mm format, or null if not selected.
   */
  String getEndTime();

  /**
   * Gets the days of the week selected for recurring events.
   *
   * @return a boolean array where index 0 is Sunday, 1 is Monday, etc.
   */
  boolean[] getSelectedDays();

  /**
   * Checks if the recurrence should end by occurrence count.
   *
   * @return true if ending by occurrence count, false if ending by date.
   */
  boolean isEndByOccurrence();

  /**
   * Gets the number of occurrences for recurring events.
   *
   * @return the number of times the event should repeat.
   */
  int getOccurrences();

  /**
   * Gets the end date for recurring events.
   *
   * @return the date when recurring events should stop.
   */
  LocalDate getEndDate();

  /**
   * Gets the list of selected event titles from the event list.
   *
   * @return a list of event titles that are currently selected.
   */
  List<String> getSelectedEvents();
}