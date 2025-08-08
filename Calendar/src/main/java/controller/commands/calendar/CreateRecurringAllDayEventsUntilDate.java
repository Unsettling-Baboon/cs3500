package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class CreateRecurringAllDayEventsUntilDate extends CalCommand {
  public CreateRecurringAllDayEventsUntilDate(
          RobustCalendar m,
          String eventSubject,
          String dateString,
          String repeatWeekdays,
          String untilDateString
  ) {
    super(m);
    this.eventSubject = eventSubject;
    this.dateString = dateString;
    this.repeatWeekdays = repeatWeekdays;
    this.untilDateString = untilDateString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^create event (.*?) on (\\S+) repeats (\\S+) until (\\S+)$"
    );
    return pattern.matcher(prompt);
  }

  public static boolean matches(String prompt) {
    return matcher(prompt).matches();
  }

  public static void perform(
          String prompt,
          RobustCalendar r,
          Appendable out
  ) throws IOException {
    Matcher matcher = matcher(prompt);
    boolean matches = matcher.matches(); // CRUCIAL to ensure matcher groups populate
    String eventSubject = matcher.group(1).trim();
    String date = matcher.group(2);
    String weekDays = matcher.group(3);
    String untilDate = matcher.group(4);
    new CreateRecurringAllDayEventsUntilDate(
            r,
            eventSubject,
            date,
            weekDays,
            untilDate
    ).perform();
    out.append(
            String.format(
                    "Created series with subjects \"%s\" "
                            + "starting on %s and repeating"
                            + "until %s%s",
                    eventSubject,
                    date,
                    untilDate,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createRecurringAllDayEventsUntilDate(
            eventSubject,
            dateString,
            repeatWeekdays,
            untilDateString
    );
  }
}
