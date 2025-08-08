package controller.calendars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import model.calendars.MockCalendars;

/**
 * A JUnit 5 test class for the {@link CalendarsControllerImpl} class.
 */
public class CalendarsControllerImplTest {
  private StringReader input;
  private StringWriter output;
  private MockCalendars mockModel;
  private CalendarsController controller;

  @BeforeEach
  public void setUp() {
    // initializes input, output, mock model, and controller for each test.
    input = new StringReader("");
    output = new StringWriter();
    mockModel = new MockCalendars();
    controller = new CalendarsControllerImpl(input, output, mockModel);
  }

  @Test
  public void testWelcomeAndMenuPrintOnStart() throws IOException {
    // verifies that the welcome message and menu are printed when the controller starts.
    input = new StringReader("exit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedOutputStart = System.lineSeparator() + "Welcome to the calendar program!" +
            System.lineSeparator() + System.lineSeparator();
    assertTrue(output.toString().startsWith(expectedOutputStart));
    assertTrue(output.toString().contains("Please enter your choice: "));
  }

  @Test
  public void testExitCommand() throws IOException {
    // checks that the controller exits gracefully when "exit" is entered.
    input = new StringReader("exit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    assertFalse(output.toString().contains("ACTION FAILED:"));
  }

  @Test
  public void testPrintMenuCommand() throws IOException {
    // ensures that the menu is reprinted when the "print menu" command is given.
    input = new StringReader("print menu\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String fullOutput = output.toString();
    int firstMenuIndex = fullOutput.indexOf(getExpectedMenu());
    int secondMenuIndex = fullOutput.indexOf(getExpectedMenu(), firstMenuIndex + 1);

    assertTrue(firstMenuIndex != -1);
    assertTrue(secondMenuIndex != -1);
  }

  // helper method to get the expected menu string
  private String getExpectedMenu() {
    return "Your choices are as follows: " + System.lineSeparator() + System.lineSeparator() +
            "print menu" + System.lineSeparator() + System.lineSeparator() +
            "(You must first create a calendar before creating events.)" +
            System.lineSeparator() + System.lineSeparator() +
            "create calendar --name <calName> --timezone <area/location>" +
            System.lineSeparator() + System.lineSeparator() +
            "edit calendar --name <calName> --property <property> <newPropertyValue>" +
            System.lineSeparator() + System.lineSeparator() +
            "use calendar --name <calName>" +
            System.lineSeparator() + System.lineSeparator() +
            "copy event <eventName> on <dateStringTtimeString> --target <calName> to <dateStringTtimeString>" +
            System.lineSeparator() +
            "copy events on <dateString> --target <calendarName> to <dateString>" +
            System.lineSeparator() +
            "copy events between <dateString> and <dateString> --target <calName> to <dateString>" +
            System.lineSeparator() + System.lineSeparator() +
            "create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>" +
            System.lineSeparator() +
            "create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times" +
            System.lineSeparator() +
            "create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateString>" +
            System.lineSeparator() +
            "create event <eventSubject> on <dateString>" +
            System.lineSeparator() +
            "create event <eventSubject> on <dateString> repeats <weekdays> for <N> times" +
            System.lineSeparator() +
            "create event <eventSubject> on <dateString> repeats <weekdays> until <dateString>" +
            System.lineSeparator() + System.lineSeparator() +
            "edit event <property> <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> with <newPropertyValue>" +
            System.lineSeparator() +
            "edit events <property> <eventSubject> from <dateStringTtimeString> with <newPropertyValue>" +
            System.lineSeparator() +
            "edit series <property> <eventSubject> from <dateStringTtimeString> with <newPropertyValue>" +
            System.lineSeparator() + System.lineSeparator() +
            "print events on <dateString>" +
            System.lineSeparator() +
            "print events from <dateStringTtimeString> to <dateStringTtimeString>" +
            System.lineSeparator() + System.lineSeparator() +
            "show status on <dateStringTtimeString>" +
            System.lineSeparator() + System.lineSeparator() +
            "exit" + System.lineSeparator() + System.lineSeparator() +
            "Note that <dateString> is a String of the form \"YYYY-MM-DD\", " +
            "<timeString> is a String of the form \"hh:mm\", " + System.lineSeparator() +
            "and <dateStringTtimeString> is a String of the form \"YYYY-MM-DDThh:mm\"." +
            System.lineSeparator() + System.lineSeparator() +
            "For calendars, the <property> field may be one of either \"name\" or \"timezone\"; for events, it may be one of " +
            "the following:" + System.lineSeparator() +
            " \"subject\", \"start\", \"end\", \"description\", \"location\", or \"status\". " +
            System.lineSeparator() + System.lineSeparator() +
            "For calendars, the format of the new property values are <string> and <string>, " +
            "respectively, but for events, the formats, " + System.lineSeparator() +
            "respectively, are: <string>, <dateStringTtimeString>, <dateStringTtimeString>, " +
            "<string>, <string>, and <string>. Subject " + System.lineSeparator() +
            "and description may be anything, but location must be one of \"physical\" or \"online\", " +
            "and status must be one of \"status\" " + System.lineSeparator() +
            "or \"private\"." + System.lineSeparator() +
            System.lineSeparator() + "Please enter your choice: ";
  }

  @Test
  public void testCreateCalendarCommand() throws IOException {
    // verifies that the controller correctly parses and calls `createCalendar` on the model.
    input = new StringReader("create calendar --name MyCal --timezone America/Los_Angeles\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "createCalendar(name='MyCal', timeZone='America/Los_Angeles') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testEditCalendarCommand() throws IOException {
    // ensures the controller correctly calls `editCalendar` on the model with parsed arguments.
    input = new StringReader("edit calendar --name OldCalName --property name NewCalName\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "editCalendar(name='OldCalName', property='name', newPropertyVal='NewCalName') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testUseCalendarCommand() throws IOException {
    // checks that the controller calls `useCalendar` on the model as expected.
    input = new StringReader("use calendar --name MyNewCalendar\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "useCalendar(name='MyNewCalendar') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testCopySingleEventCommand() throws IOException {
    // validates that `copySingleEvent` is called on the model with correct parameters.
    input = new StringReader("copy event MyEvent on 2025-06-15T10:00 --target TargetCal to 2025-06-16T11:00\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "copySingleEvent(name='MyEvent', fromDate='2025-06-15T10:00', calName='TargetCal', newFromDate='2025-06-16T11:00') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testCopyAllEventsOnDayCommand() throws IOException {
    // asserts that `copyAllEventsOnDay` is invoked on the model with the correct arguments.
    input = new StringReader("copy events on 2025-06-15 --target TargetCal to 2025-06-17\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "copyAllEventsOnDay(onDate='2025-06-15', calName='TargetCal', toDate='2025-06-17') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testCopyAllEventsInBetweenDatesCommand() throws IOException {
    // confirms `copyAllEventsInBetweenDates` is called on the model with the specified range and target.
    input = new StringReader("copy events between 2025-06-01 and 2025-06-30 --target TargetCal to 2025-07-01\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "copyAllEventsInBetweenDates(fromDate='2025-06-01', toDate='2025-06-30', calName='TargetCal', newDate='2025-07-01') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testUnknownCommand() throws IOException {
    // checks that the controller handles unrecognized commands by printing an error message.
    input = new StringReader("some unknown command\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    assertTrue(output.toString().contains("Unfortunately, no actions matched. Please try again.\n"));
    assertTrue(output.toString().contains("Please enter your next choice: "));
    assertTrue(mockModel.getLog().isEmpty());
  }

  @Test
  public void testModelThrowsNoSuchElementExceptionForGetActiveCal() throws IOException {
    // verifies the controller catches and reports `NoSuchElementException` from `getActiveCal()`.
    input = new StringReader("print events on 2025-01-01\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    assertTrue(mockModel.getLog().contains("getActiveCal() called.\n"));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }

  @Test
  public void testModelDoesNotThrowForEditCalendarWithInvalidArguments() throws IOException {
    // ensures the controller handles `IllegalArgumentException` during an edit operation.
    input = new StringReader("edit calendar --name MyCal --property invalidProperty someValue\nexit\n");
    controller = new CalendarsControllerImpl(input, output, mockModel);

    controller.go();

    String expectedLog = "editCalendar(name='MyCal', property='invalidProperty', newPropertyVal='someValue') called.\n";
    assertTrue(mockModel.getLog().contains(expectedLog));
    assertTrue(output.toString().contains("Edited the invalidProperty property of calendar with name MyCal with someValue"));
    assertTrue(output.toString().contains("Please enter your next choice: "));
  }
}