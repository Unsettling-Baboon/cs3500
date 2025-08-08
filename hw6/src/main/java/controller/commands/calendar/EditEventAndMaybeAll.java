package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class EditEventAndMaybeAll extends CalCommand {
  public EditEventAndMaybeAll(
          RobustCalendar m,
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String newPropertyVal
  ) {
    super(m);
    this.property = property;
    this.eventSubject = eventSubject;
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.newPropertyVal = newPropertyVal;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^edit series (.*?) (\\S+) from (\\S+) with (\\S+)$"
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
    String property = matcher.group(1).trim();
    String eventSubject = matcher.group(2).trim();
    String fromDateTime = matcher.group(3);
    String newVal = matcher.group(4);
    new EditEventAndMaybeAll(
            r,
            property,
            eventSubject,
            fromDateTime,
            newVal
    ).perform();
    out.append(
            String.format(
                    "Edited %s of all events with subjects \"%s\" "
                            + "in series (if any)%s",
                    property,
                    eventSubject,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.editEventAndMaybeAll(
            property,
            eventSubject,
            fromDateStringTtimeString,
            newPropertyVal
    );
  }
}
