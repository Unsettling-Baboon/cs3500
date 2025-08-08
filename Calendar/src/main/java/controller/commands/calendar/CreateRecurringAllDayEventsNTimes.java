package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class CreateRecurringAllDayEventsNTimes extends CalCommand {
  public CreateRecurringAllDayEventsNTimes(
          RobustCalendar m,
          String eventSubject,
          String dateString,
          String repeatWeekdays,
          int N
  ) {
    super(m);
    this.eventSubject = eventSubject;
    this.dateString = dateString;
    this.repeatWeekdays = repeatWeekdays;
    this.N = N;
  }

  public static Matcher matcher(String prompt) {
    Pattern allDaySeriesCreate = Pattern.compile(
            "^create event (.*?) on (\\S+) repeats (\\S+) for (\\S+) times$"
    );
    return allDaySeriesCreate.matcher(prompt);
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
    int N = Integer.parseInt(matcher.group(4));
    new CreateRecurringAllDayEventsNTimes(
            r,
            eventSubject,
            date,
            weekDays,
            N
    ).perform();
    out.append(
            String.format(
                    "Created series with subjects \"%s\" "
                            + "starting on %s and repeating %d "
                            + "times%s",
                    eventSubject,
                    date,
                    N,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createRecurringAllDayEventsNTimes(
            eventSubject,
            dateString,
            repeatWeekdays,
            N
    );
  }
}
