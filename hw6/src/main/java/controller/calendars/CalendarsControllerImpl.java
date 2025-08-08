package controller.calendars;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

import controller.calendar.CalendarController;
import controller.calendar.CalendarControllerImpl;
import controller.commands.calendars.CopyAllEventsInBetweenDates;
import controller.commands.calendars.CopyAllEventsOnDay;
import controller.commands.calendars.CopySingleEvent;
import controller.commands.calendars.CreateCalendar;
import controller.commands.calendars.EditCalendar;
import controller.commands.calendars.UseCalendar;
import model.calendar.CalendarImpl;
import model.calendars.RobustCalendars;

public class CalendarsControllerImpl
        extends CalendarControllerImpl implements CalendarsController {
  private final Scanner in;
  private final Appendable out;
  private final RobustCalendars m;
  private CalendarController c;
  private final Set<Function<String, Boolean>> oldChecks;
  private final Map<Function<String, Boolean>,
          Function<String, Function<RobustCalendars, Function<Appendable, Void>>>> commands;

  public CalendarsControllerImpl(
          Readable in,
          Appendable out,
          RobustCalendars m
  ) {
    super(in, out, new CalendarImpl());
    this.in = new Scanner(in);
    this.out = out;
    this.m = m;
    this.c = new CalendarControllerImpl(in, out, new CalendarImpl());
    oldChecks = super.putCalCommands().keySet();
    commands = putCalsCommands();
  }

  private Map<Function<String, Boolean>,
          Function<String, Function<RobustCalendars, Function<Appendable, Void>>>> putCalsCommands() {
    final Map<Function<String, Boolean>,
            Function<String, Function<RobustCalendars, Function<Appendable, Void>>>> commands =
            new LinkedHashMap<>();
    commands.putAll(
            Map.of(
                    CreateCalendar::matches, p -> r -> o ->
                    {
                      try {
                        CreateCalendar.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    EditCalendar::matches, p -> r -> o ->
                    {
                      try {
                        EditCalendar.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CopySingleEvent::matches, p -> r -> o ->
                    {
                      try {
                        CopySingleEvent.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CopyAllEventsOnDay::matches, p -> r -> o ->
                    {
                      try {
                        CopyAllEventsOnDay.perform(p, r, o);
                      } catch (IOException i) {
                        throw new IllegalArgumentException(i.getMessage());
                      }
                      return null;
                    },
                    CopyAllEventsInBetweenDates::matches, p -> r -> o ->
                    {
                      try {
                        CopyAllEventsInBetweenDates.perform(p, r, o);
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
  public void go() throws IOException {
    out.append(System.lineSeparator()).append("Welcome to the calendar program!")
            .append(System.lineSeparator()).append(System.lineSeparator());
    printMenu();
    while (in.hasNextLine()) {
      try {
        String prompt = in.nextLine().trim();
        if (prompt.equals("exit")) {
          return;
        } else if (prompt.equals("print menu")) {
          printMenu();
        } else if (UseCalendar.matches(prompt)) {
          c = UseCalendar.perform(prompt, m, out, c);
          out.append("Please enter your next choice: ");
        } else {
          if (oldChecks.stream().anyMatch((it) -> it.apply(prompt))) {
            m.getActiveCal();
            if (c.goOnce(prompt)) {
              return;
            }
          } else {
            boolean matched = false;
            for (Entry<Function<String, Boolean>,
                    Function<String, Function<RobustCalendars, Function<Appendable, Void>>>>
                    command : commands.entrySet()) {
              if (command.getKey().apply(prompt)) {
                command.getValue().apply(prompt).apply(m).apply(out);
                matched = true;
                break;
              }
            }
            if (!matched) {
              out.append("Unfortunately, no actions matched. Please try again.\n");
            }
          }
          out.append("Please enter your next choice: ");
        }
      } catch (Exception e) {
        out.append("ACTION FAILED: ").append(e.getMessage()).append(System.lineSeparator());
        out.append("Please enter your next choice: ");
      }
    }
  }

  @Override
  public void printMenu() throws IOException {
    out.append("Your choices are as ")
            .append("follows: ").append(System.lineSeparator()).append(System.lineSeparator())
            .append("print menu").append(System.lineSeparator()).append(System.lineSeparator())
            .append("(You must first create a calendar before creating " +
                    "events.)").append(System.lineSeparator()).append(System.lineSeparator())
            .append("create calendar --name <calName> --timezone <area/" +
                    "location>").append(System.lineSeparator()).append(System.lineSeparator())
            .append("edit calendar --name <calName> --property <property> " +
                    "<newPropertyValue>"
            ).append(System.lineSeparator()).append(System.lineSeparator())
            .append("use calendar --name <calName>"
            ).append(System.lineSeparator()).append(System.lineSeparator())
            .append("copy event <eventName> on <dateStringTtimeString> --target " +
                    "<calName> to <dateStringTtimeString>").append(System.lineSeparator())
            .append("copy events on <dateString> --target <calendarName> " +
                    "to <dateString>").append(System.lineSeparator())
            .append("copy events between <dateString> and <dateString> --target <calName> to " +
                    "<dateString>").append(System.lineSeparator()).append(System.lineSeparator())
            .append("create event <eventSubject> from <dateStringTtimeString>" +
                    " to <dateStringTtimeString>").append(System.lineSeparator())
            .append("create event <eventSubject> from <dateStringTtimeString> " +
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
                    "until " +
                    "<dateString>").append(System.lineSeparator()).append(System.lineSeparator())
            .append("edit event <property> <eventSubject> from <dateStringTtimeString> " +
                    "to <dateStringTtimeString> " +
                    "with <newPropertyValue>").append(System.lineSeparator())
            .append("edit events <property> <eventSubject> from <dateStringTtimeString> " +
                    "with <newPropertyValue>").append(System.lineSeparator())
            .append("edit series <property> <eventSubject> from <dateStringTtimeString> " +
                    "with <newPropertyValue>"
            ).append(System.lineSeparator()).append(System.lineSeparator())
            .append("print events on <dateString>").append(System.lineSeparator())
            .append("print events from <dateStringTtimeString> " +
                    "to <dateStringTtimeString>"
            ).append(System.lineSeparator()).append(System.lineSeparator())
            .append("show status on <dateStringTtimeString>"
            ).append(System.lineSeparator()).append(System.lineSeparator())
            .append("exit").append(System.lineSeparator()).append(System.lineSeparator())
            .append("Note that <dateString> is a String of the form \"YYYY-MM-DD\", " +
                    "<timeString> is a String of the " +
                    "form \"hh:mm\", ").append(System.lineSeparator())
            .append("and <dateStringTtimeString> is a String of the form \"YYYY-MM-DDThh" +
                    ":mm\".").append(System.lineSeparator()).append(System.lineSeparator())
            .append("For calendars, the <property> field may be one of either \"name\" or" +
                    " \"timezone\"; for events, it may be one of " +
                    "the following:").append(System.lineSeparator())
            .append(" \"subject\", \"start\", \"end\", \"description\", \"location\", or " +
                    "\"status\". ").append(System.lineSeparator()).append(System.lineSeparator())
            .append("For calendars, the format of the new property values are <string> and" +
                    " <string>, respectively, but for events, " +
                    "the formats, ").append(System.lineSeparator())
            .append("respectively, are: <string>, <dateStringTtimeString>, <dateStringTtimeString>, " +
                    "<string>, <string>, and <string>. Subject ").append(System.lineSeparator())
            .append("and description may be anything, but location must " +
                    "be one of \"physical\" or \"online\", and status must be one " +
                    "of \"status\" ").append(System.lineSeparator())
            .append("or \"private\".").append(System.lineSeparator())
            .append(System.lineSeparator()).append("Please enter your choice: ");
  }
}
