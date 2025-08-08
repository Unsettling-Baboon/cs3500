package controller.commands.calendars;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendars.RobustCalendars;

public class CopyAllEventsInBetweenDates extends CalsCommand {
  public CopyAllEventsInBetweenDates(
          RobustCalendars m,
          String fromDateString,
          String toDateString,
          String calName,
          String newDateString
  ) {
    super(m);
    this.fromDateString = fromDateString;
    this.toDateString = toDateString;
    this.calName = calName;
    this.newDateString = newDateString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^copy events between (.*?) and (\\S+) --target (\\S+) to (\\S+)$"
    );
    return pattern.matcher(prompt);
  }

  public static boolean matches(String prompt) {
    return matcher(prompt).matches();
  }

  public static void perform(
          String prompt,
          RobustCalendars m,
          Appendable out
  ) throws IOException {
    Matcher matcher = matcher(prompt);
    boolean matches = matcher.matches(); // CRUCIAL to ensure matcher groups populate
    String fromDateString = matcher.group(1).trim();
    String toDateString = matcher.group(2);
    String calName = matcher.group(3);
    String newDateString = matcher.group(4);
    new CopyAllEventsInBetweenDates(
            m,
            fromDateString,
            toDateString,
            calName,
            newDateString
    ).perform();
    out.append(
            String.format(
                    "Copied events in between %s and %s to %s in calendar %s%s",
                    fromDateString,
                    toDateString,
                    newDateString,
                    calName,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.copyAllEventsInBetweenDates(
            fromDateString,
            toDateString,
            calName,
            newDateString
    );
  }
}
