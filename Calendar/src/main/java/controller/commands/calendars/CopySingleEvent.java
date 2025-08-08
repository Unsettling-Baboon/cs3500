package controller.commands.calendars;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendars.RobustCalendars;

public class CopySingleEvent extends CalsCommand {
  public CopySingleEvent(
          RobustCalendars m,
          String name,
          String fromDateStringTtimeString,
          String calName,
          String newFromDateStringTtimeString
  ) {
    super(m);
    this.name = name;
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.calName = calName;
    this.newFromDateStringTtimeString = newFromDateStringTtimeString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^copy event (.*?) on (\\S+) --target (\\S+) to (\\S+)$"
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
    String name = matcher.group(1).trim();
    String fromDateStringTtimeString = matcher.group(2);
    String calName = matcher.group(3);
    String newFromDateStringTtimeString = matcher.group(4);
    new CopySingleEvent(
            r,
            name,
            fromDateStringTtimeString,
            calName,
            newFromDateStringTtimeString
    ).perform();
    out.append(
            String.format(
                    "Copied event %s on specified date " +
                            "%s to new date %s in calendar %s%s",
                    name,
                    fromDateStringTtimeString,
                    newFromDateStringTtimeString,
                    calName,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.copySingleEvent(
            name,
            fromDateStringTtimeString,
            calName,
            newFromDateStringTtimeString
    );
  }
}
