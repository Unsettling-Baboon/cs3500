package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class EditEventAndMaybeOnward extends CalCommand {
  public EditEventAndMaybeOnward(
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
            "^edit events (.*?) (\\S+) from (\\S+) with (\\S+)$"
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
    new EditEventAndMaybeOnward(
            r,
            property,
            eventSubject,
            fromDateTime,
            newVal
    ).perform();
    out.append(
            String.format(
                    "Edited %s of events with subjects \"%s\" "
                            + "in series (if any) starting from %s%s",
                    property,
                    eventSubject,
                    fromDateTime,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.editEventAndMaybeOnward(
            property,
            eventSubject,
            fromDateStringTtimeString,
            newPropertyVal
    );
  }
}
