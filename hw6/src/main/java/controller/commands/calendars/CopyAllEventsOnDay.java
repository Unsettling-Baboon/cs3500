package controller.commands.calendars;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendars.RobustCalendars;

public class CopyAllEventsOnDay extends CalsCommand {
  public CopyAllEventsOnDay(
          RobustCalendars m,
          String onDateString,
          String calName,
          String toDateString
  ) {
    super(m);
    this.onDateString = onDateString;
    this.calName = calName;
    this.toDateString = toDateString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^copy events on (.*?) --target (\\S+) to (\\S+)$"
    );
    return pattern.matcher(prompt);
  }

  public static boolean matches(String prompt) {
    return matcher(prompt).matches();
  }

  public static void perform(
          String prompt,
          RobustCalendars r,
          Appendable out
  ) throws IOException {
    Matcher matcher = matcher(prompt);
    boolean matches = matcher.matches(); // CRUCIAL to ensure matcher groups populate
    String onDateString = matcher.group(1).trim();
    String calName = matcher.group(2);
    String toDateString = matcher.group(3);
    new CopyAllEventsOnDay(r, onDateString, calName, toDateString).perform();
    out.append(
            String.format(
                    "Copied events on %s to %s in calendar %s%s",
                    onDateString,
                    toDateString,
                    calName,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.copyAllEventsOnDay(
            onDateString,
            calName,
            toDateString
    );
  }
}
