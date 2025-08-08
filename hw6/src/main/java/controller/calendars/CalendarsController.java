package controller.calendars;

import java.io.IOException;

/**
 * An interface representing what a controller for the multi-calendar program
 * must be able to carry out.
 */
public interface CalendarsController {
  /**
   * Execute the calendar program and let the user input relevant information.
   *
   * @throws IOException for an inability to append output messages.
   */
  void go() throws IOException;

  void printMenu() throws IOException;
}
