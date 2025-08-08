package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class CreateRecurringNormalEventsNTimes extends CalCommand {
  public CreateRecurringNormalEventsNTimes(
          RobustCalendar m,
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String repeatWeekdays,
          int N
  ) {
    super(m);
    this.eventSubject = eventSubject;
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.toDateStringTtimeString = toDateStringTtimeString;
    this.repeatWeekdays = repeatWeekdays;
    this.N = N;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^create event (.*?) from (\\S+) to (\\S+) "
                    + "repeats (\\S+) for (\\S+) times$"
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
    int N = Integer.parseInt(matcher.group(5));
    new CreateRecurringNormalEventsNTimes(
            r,
            eventSubject,
            fromDateTime,
            toDateTime,
            weekDays,
            N
    ).perform();
    out.append(
            String.format(
                    "Created series with subjects \"%s\" "
                            + "starting from %s to %s and repeating "
                            + "%d times%s",
                    eventSubject,
                    fromDateTime,
                    toDateTime,
                    N,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createRecurringNormalEventsNTimes(
            eventSubject,
            fromDateStringTtimeString,
            toDateStringTtimeString,
            repeatWeekdays,
            N
    );
  }
}
