package controller.calendar;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Function;

import model.calendar.RobustCalendar;
import controller.commands.calendar.CreateRecurringAllDayEventsNTimes;
import controller.commands.calendar.CreateRecurringAllDayEventsUntilDate;
import controller.commands.calendar.CreateRecurringNormalEventsNTimes;
import controller.commands.calendar.CreateRecurringNormalEventsUntilDate;
import controller.commands.calendar.CreateSingleAllDayEvent;
import controller.commands.calendar.CreateSingleNormalEvent;
import controller.commands.calendar.EditEventAndMaybeAll;
import controller.commands.calendar.EditEventAndMaybeOnward;
import controller.commands.calendar.EditSingleEvent;
import controller.commands.calendar.PrintAllEventsBetweenDates;
import controller.commands.calendar.PrintAllEventsOnDate;
import controller.commands.calendar.ShowStatusOnDateAtTime;

public class CalendarControllerImpl implements CalendarController {
  private final Readable input;
  private final Scanner in;
  private final Appendable out;
  private final RobustCalendar m;
  protected final Map<Function<String, Boolean>,
          Function<String, Function<RobustCalendar, Function<Appendable, Void>>>> commands;

  public CalendarControllerImpl(
          Readable in,
          Appendable out,
          RobustCalendar m
  ) {
    this.input = in;
    this.in = new Scanner(in);
    this.out = out;
    this.m = m;
    commands = putCalCommands();
  }

  protected Map<Function<String, Boolean>,
          Function<String, Function<RobustCalendar, Function<Appendable, Void>>>> putCalCommands()
  {
    final Map<Function<String, Boolean>,
            Function<String, Function<RobustCalendar, Function<Appendable, Void>>>> commands;
    commands = new LinkedHashMap<>(
            Map.of(
                    "print menu"::equals, p -> r -> o ->
                    {
                      try {
                        printMenu();
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                      return null;
                    },
                    CreateSingleNormalEvent::matches, p -> r -> o ->
                    {
                      try {
                        CreateSingleNormalEvent.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CreateRecurringNormalEventsNTimes::matches, p -> r -> o ->
                    {
                      try {
                        CreateRecurringNormalEventsNTimes.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CreateRecurringNormalEventsUntilDate::matches, p -> r -> o ->
                    {
                      try {
                        CreateRecurringNormalEventsUntilDate.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CreateSingleAllDayEvent::matches, p -> r -> o ->
                    {
                      try {
                        CreateSingleAllDayEvent.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CreateRecurringAllDayEventsNTimes::matches, p -> r -> o ->
                    {
                      try {
                        CreateRecurringAllDayEventsNTimes.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CreateRecurringAllDayEventsUntilDate::matches, p -> r -> o ->
                    {
                      try {
                        CreateRecurringAllDayEventsUntilDate.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    EditSingleEvent::matches, p -> r -> o ->
                    {
                      try {
                        EditSingleEvent.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    EditEventAndMaybeOnward::matches, p -> r -> o ->
                    {
                      try {
                        EditEventAndMaybeOnward.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    EditEventAndMaybeAll::matches, p -> r -> o ->
                    {
                      try {
                        EditEventAndMaybeAll.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    }
            )
    );
    commands.putAll(
            Map.of(
                    PrintAllEventsOnDate::matches, p -> r -> o ->
                    {
                      try {
                        PrintAllEventsOnDate.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    PrintAllEventsBetweenDates::matches, p -> r -> o ->
                    {
                      try {
                        PrintAllEventsBetweenDates.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    ShowStatusOnDateAtTime::matches, p -> r -> o ->
                    {
                      try {
                        ShowStatusOnDateAtTime.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    }
            )
    );
    return commands;
  }

  @Override
  public final CalendarController withNewCal(RobustCalendar r) {
    return new CalendarControllerImpl(input, out, r);
  }

  @Override
  public void go() throws IOException {
    out.append("Welcome to the calendar program!")
            .append(System.lineSeparator()).append(System.lineSeparator());
    printMenu();
    while (in.hasNextLine()) {
      try {
        String prompt = in.nextLine().trim();
        if (goOnce(prompt)) {
          return;
        }
        out.append("Please enter your next choice: ");
      } catch (Exception e) {
        out.append(e.getMessage()).append(System.lineSeparator());
        out.append("Please enter your next choice: ");
      }
    }
  }

  @Override
  public boolean goOnce(String prompt) throws IOException {
    boolean matched = false;
    if (prompt.equals("exit")) {
      return true;
    } else {
      for (Entry<Function<String, Boolean>,
              Function<String, Function<RobustCalendar, Function<Appendable, Void>>>>
              command : commands.entrySet()) {
        if (command.getKey().apply(prompt)) {
          command.getValue().apply(prompt).apply(m).apply(out);
          matched = true;
          break;
        }
      }
    }
    if (!matched) {
      out.append("Unfortunately, no actions matched. Please try again.\n");
    }
    return false;
  }

  @Override
  public void printMenu() throws IOException {
    out.append("Your choices are as follows: ").append(System.lineSeparator())
            .append("print menu").append(System.lineSeparator())
            .append("create event <eventSubject> from <dateStringTtimeString>" +
                    " to <dateStringTtimeString>").append(System.lineSeparator())
            .append(
                    "create event <eventSubject> from <dateStringTtimeString> " +
                            "to <dateStringTtimeString> repeats <weekdays> " +
                            "for <N> times").append(System.lineSeparator())
            .append("create event <eventSubject> from <dateStringTtimeString> " +
                    "to <dateStringTtimeString> repeats <weekdays> " +
                    "until <dateString>").append(System.lineSeparator())
            .append("create event <eventSubject> " +
                    "on <dateString>").append(System.lineSeparator())
            .append("create event <eventSubject> on <dateString> repeats <weekdays> " +
                    "for <N> times").append(System.lineSeparator())
            .append("create event <eventSubject> on <dateString> repeats <weekdays> " +
                    "until <dateString>").append(System.lineSeparator())
            .append("edit event <property> <eventSubject> from <dateStringTtimeString> " +
                    "to <dateStringTtimeString> " +
                    "with <NewPropertyValue>").append(System.lineSeparator())
            .append("edit events <property> <eventSubject> from <dateStringTtimeString> " +
                    "with <NewPropertyValue>").append(System.lineSeparator())
            .append("edit series <property> <eventSubject> from <dateStringTtimeString> " +
                    "with <NewPropertyValue>").append(System.lineSeparator())
            .append("print events on <dateString>").append(System.lineSeparator())
            .append("print events from <dateStringTtimeString> " +
                    "to <dateStringTtimeString>").append(System.lineSeparator())
            .append("show status on <dateStringTtimeString>").append(System.lineSeparator())
            .append("exit").append(System.lineSeparator()).append(System.lineSeparator())
            .append("Note that <dateString> is a String of the form \"YYYY-MM-DD\", " +
                    "<timeString> is a String of the " +
                    "form \"hh:mm\", ").append(System.lineSeparator())
            .append("and <dateStringTtimeString> is a String of the form \"YYYY-MM-DDThh" +
                    ":mm\".").append(System.lineSeparator()).append(System.lineSeparator())
            .append("The <property> field may be one of the following: subject, start, " +
                    "end, description, location, or status. ").append(System.lineSeparator())
            .append("The format of the new property values are <string>, " +
                    "<dateStringTtimeString>, <dateStringTtimeString>, " +
                    "<string>, ").append(System.lineSeparator())
            .append("<string>, and <string>, respectively. Subject and description may " +
                    "be anything, but location must be one of ").append(System.lineSeparator())
            .append("\"physical\" or \"online\", and status must be one of \"status\" " +
                    "or \"private\".").append(System.lineSeparator())
            .append(System.lineSeparator()).append("Please enter your choice: ");
  }
}
