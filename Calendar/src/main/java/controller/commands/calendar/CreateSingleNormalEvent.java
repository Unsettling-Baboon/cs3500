package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class CreateSingleNormalEvent extends CalCommand {
  public CreateSingleNormalEvent(
          RobustCalendar m,
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  ) {
    super(m);
    this.eventSubject = eventSubject;
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.toDateStringTtimeString = toDateStringTtimeString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^create event (.*?) from (\\S+) to (\\S+)$"
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
    new CreateSingleNormalEvent(
            r,
            eventSubject,
            fromDateTime,
            toDateTime
    ).perform();
    out.append(
            String.format(
                    "Created \"%s\" from %s to %s%s",
                    eventSubject,
                    fromDateTime,
                    toDateTime,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createSingleNormalEvent(
            eventSubject,
            fromDateStringTtimeString,
            toDateStringTtimeString
    );
  }
}
