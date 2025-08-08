package controller.calendar;

import java.io.IOException;

import model.calendar.RobustCalendar;

/**
 * An interface representing what a controller for the calendar program must
 * be able to carry out.
 */
public interface CalendarController {
  /**
   * Return a new instance of this controller with everything the same except
   * for storing a new calendar.
   *
   * @param r the new calendar to store.
   * @return the new instance of a controller with a new calendar.
   */
  CalendarController withNewCal(RobustCalendar r);

  /**
   * Execute the calendar program and let the user input relevant information.
   *
   * @throws IOException for an inability to append output messages.
   */
  void go() throws IOException;

  /**
   * Do a single command based on the user input.
   *
   * @param prompt the prompt entered into this turn of the user.
   * @return whether to terminate the program or not.
   * @throws IOException if the target output is flawed.
   */
  boolean goOnce(String prompt) throws IOException;

  /**
   * Print the menu of all supported commands.
   */
  void printMenu() throws IOException;
}
