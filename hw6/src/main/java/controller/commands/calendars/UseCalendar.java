package controller.commands.calendars;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.calendar.CalendarController;
import model.calendars.RobustCalendars;

public class UseCalendar extends CalsCommand {
  public UseCalendar(
          RobustCalendars m,
          String name
  ) {
    super(m);
    this.name = name;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^use calendar --name (\\S+)$"
    );
    return pattern.matcher(prompt);
  }

  public static boolean matches(String prompt) {
    return matcher(prompt).matches();
  }

  public static CalendarController perform(
          String prompt,
          RobustCalendars m,
          Appendable out,
          CalendarController c
  ) throws IOException {
    Matcher matcher = matcher(prompt);
    boolean matches = matcher.matches(); // CRUCIAL to ensure matcher groups populate
    String name = matcher.group(1).trim();
    new UseCalendar(m, name).perform();
    out.append(String.format("Now using the \"%s\" calendar%s", name, System.lineSeparator()));
    return c.withNewCal(m.getActiveCal());
  }

  @Override
  public void perform() {
    m.useCalendar(name);
  }
}
