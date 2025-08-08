package controller.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import model.calendar.MockCalendar;

import static org.junit.jupiter.api.Assertions.*;

class CalendarControllerImplTest {
  MockCalendar mock;
  Appendable output; // use appendable for output
  CalendarController controller;

  @BeforeEach
  void setUp() {
    mock = new MockCalendar();
    output = new StringWriter(); // capture output to a stringwriter
  }

  // helper to create a controller for a given input string
  private CalendarController createController(String input) {
    return new CalendarControllerImpl(new StringReader(input), output, mock);
  }

  // helper to get the captured output
  private String getOutput() {
    return output.toString();
  }

  // initial welcome and menu tests
  @Test
  void goPrintsWelcomeAndMenuOnStart() throws IOException {
    controller = createController("exit\n"); // provide "exit" to terminate gracefully
    controller.go();

    String expectedWelcome = "Welcome to the calendar program!\n\n";
    assertTrue(getOutput().startsWith(expectedWelcome));
    assertTrue(getOutput().contains("Your choices are as follows:")); // check for menu
    assertTrue(getOutput().contains("Please enter your choice: ")); // check for prompt
  }

  @Test
  void goPrintsMenuOnExplicitCommand() throws IOException {
    String input = "print menu\nexit\n"; // first print menu then exit
    controller = createController(input);
    controller.go();
    // the output should contain the menu twice once on startup once on explicit command
    String outputString = getOutput();
    int firstMenuIndex = outputString.indexOf("Your choices are as follows:");
    int secondMenuIndex = outputString.indexOf("Your choices are as follows:", firstMenuIndex + 1);
    assertTrue(firstMenuIndex != -1);
    assertTrue(secondMenuIndex != -1);
    assertTrue(secondMenuIndex > firstMenuIndex); // ensure it's the second menu print
  }

  @Test
  void goExitsOnExitCommand() throws IOException {
    String input = "exit\n";
    controller = createController(input);
    controller.go();
    // the controller should exit without further prompts if "exit" is the first command
    assertFalse(getOutput().contains("Unfortunately no actions matched"));
    assertTrue(getOutput().contains("Welcome to the calendar program!")); // initial welcome and menu
    assertTrue(getOutput().endsWith("Please enter your choice: ")); // final prompt before exit in go()
  }

  // create event tests

  @Test
  void goCreateSingleNormalEventCallsModelAndPrintsConfirmation() throws IOException {
    String input = "create event Team Sync from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    controller = createController(input);
    controller.go();
    assertTrue(mock.getLog().contains("createSingleNormalEvent(Team Sync, 2025-06-10T09:00, 2025-06-10T10:00)"));
    assertTrue(getOutput().contains("Created \"Team Sync\" from 2025-06-10T09:00 to 2025-06-10T10:00"));
  }

  @Test
  void goCreateRecurringNormalEventsNTimesCallsModelAndPrintsConfirmation() throws IOException {
    String input = "create event Daily Standup from 2025-06-09T09:00 to 2025-06-09T09:15 repeats MRF for 5 times\nexit\n";
    controller = createController(input);
    controller.go();
    assertTrue(mock.getLog().contains("createRecurringNormalEventsNTimes(Daily Standup, 2025-06-09T09:00, 2025-06-09T09:15, MRF, 5)"));
    assertTrue(getOutput().contains("Created series with subjects \"Daily Standup\" starting from 2025-06-09T09:00 to 2025-06-09T09:15 and repeating 5 times"));
  }

  @Test
  void goCreateRecurringNormalEventsUntilDateCallsModelAndPrintsConfirmation() throws IOException {
    String input = "create event Project Review from 2025-06-01T14:00 to 2025-06-01T15:00 repeats W until 2025-07-01\nexit\n";
    controller = createController(input);
    controller.go();
    assertTrue(mock.getLog().contains("createRecurringNormalEventsUntilDate(Project Review, 2025-06-01T14:00, 2025-06-01T15:00, W, 2025-07-01)"));
    assertTrue(getOutput().contains("Created series with subjects \"Project Review\" starting from 2025-06-01T14:00 to 2025-06-01T15:00 and repeating until 2025-07-01"));
  }

  @Test
  void goInvalidCommandFormatPrintsErrorMessage() throws IOException {
    String input = "create event \"Invalid Event\" from 2025-06-10 09:00 to 2025-06-10T10:00\nexit\n"; // incorrect time format
    controller = createController(input);
    controller.go();

    assertFalse(mock.getLog().contains("createSingleNormalEvent")); // should not call the model method
    assertTrue(getOutput().contains("Unfortunately, no actions matched. Please try again."));
  }

  @Test
  void goUnrecognizedCommandPrintsErrorMessage() throws IOException {
    String input = "nonsense command\nexit\n";
    controller = createController(input);
    controller.go();
    assertTrue(getOutput().contains("Unfortunately, no actions matched. Please try again."));
  }

  @Test
  void goModelThrowsExceptionControllerCatchesAndPrintsError() throws IOException {
    // make the mock throw an exception for a specific call
    mock = new MockCalendar() {
      @Override
      public void createSingleNormalEvent(String eventSubject, String fromDateStringTtimeString, String toDateStringTtimeString) {
        throw new IllegalArgumentException("test error event creation failed!");
      }
    };
    controller = createController("create event \"Broken Event\" from 2025-06-01T08:00 to 2025-06-01T09:00\nexit\n");
    controller.go();
    assertTrue(getOutput().contains("test error event creation failed!"));
    assertTrue(getOutput().contains("Please enter your next choice: ")); // should prompt again after error
  }
}