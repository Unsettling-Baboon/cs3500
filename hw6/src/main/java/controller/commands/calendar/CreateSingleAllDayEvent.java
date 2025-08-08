package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class CreateSingleAllDayEvent extends CalCommand {
  public CreateSingleAllDayEvent(
          RobustCalendar m,
          String eventSubject,
          String dateString
  ) {
    super(m);
    this.eventSubject = eventSubject;
    this.dateString = dateString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^create event (.*?) on (\\S+)$"
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
    new CreateSingleAllDayEvent(
            r,
            eventSubject,
            date
    ).perform();
    out.append(
            String.format(
                    "Created \"%s\" on %s%s",
                    eventSubject,
                    date,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createSingleAllDayEvent(
            eventSubject,
            dateString
    );
  }
}
