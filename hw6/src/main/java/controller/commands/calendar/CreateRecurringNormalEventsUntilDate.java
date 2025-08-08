package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class CreateRecurringNormalEventsUntilDate extends CalCommand {
  public CreateRecurringNormalEventsUntilDate(
          RobustCalendar m,
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String repeatWeekdays,
          String untilDateString
  ) {
    super(m);
    this.eventSubject = eventSubject;
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.toDateStringTtimeString = toDateStringTtimeString;
    this.repeatWeekdays = repeatWeekdays;
    this.untilDateString = untilDateString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^create event (.*?) from (\\S+) to "
                    + "(\\S+) repeats (\\S+) until (\\S+)$"
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
    String fromDateTime = matcher.group(2);
    String toDateTime = matcher.group(3);
    String weekDays = matcher.group(4);
    String untilDate = matcher.group(5);
    new CreateRecurringNormalEventsUntilDate(
            r,
            eventSubject,
            fromDateTime,
            toDateTime,
            weekDays,
            untilDate
    ).perform();
    out.append(
            String.format(
                    "Created series with subjects \"%s\" "
                            + "starting from %s to %s and repeating "
                            + "until %s%s",
                    eventSubject,
                    fromDateTime,
                    toDateTime,
                    untilDate,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createRecurringNormalEventsUntilDate(
            eventSubject,
            fromDateStringTtimeString,
            toDateStringTtimeString,
            repeatWeekdays,
            untilDateString
    );
  }
}
