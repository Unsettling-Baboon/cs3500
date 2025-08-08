package controller.commands.calendars;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendars.RobustCalendars;

public class CreateCalendar extends CalsCommand {
  public CreateCalendar(
          RobustCalendars m,
          String name,
          String timeZone
  ) {
    super(m);
    this.name = name;
    this.timeZone = timeZone;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^create calendar --name (.*?) --timezone (\\S+)$"
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
    String name = matcher.group(1).trim();
    String timeZone = matcher.group(2);
    new CreateCalendar(m, name, timeZone).perform();
    out.append(
            String.format(
                    "Created calendar with name \"%s\" and time zone %s%s",
                    name,
                    timeZone,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.createCalendar(name, timeZone);
  }
}
