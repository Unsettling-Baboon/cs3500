package controller.commands.calendars;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendars.RobustCalendars;

public class EditCalendar extends CalsCommand {
  public EditCalendar(
          RobustCalendars m,
          String name,
          String property,
          String newPropertyVal
  ) {
    super(m);
    this.name = name;
    this.property = property;
    this.newPropertyVal = newPropertyVal;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^edit calendar --name (.*?) --property (.*?) (\\S+)$"
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
    String property = matcher.group(2);
    String newPropertyVal = matcher.group(3);
    new EditCalendar(m, name, property, newPropertyVal).perform();
    out.append(
            String.format(
                    "Edited the %s property of calendar with name %s with %s%s",
                    property,
                    name,
                    newPropertyVal,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.editCalendar(name, property, newPropertyVal);
  }
}
