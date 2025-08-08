package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class EditSingleEvent extends CalCommand {
  public EditSingleEvent(
          RobustCalendar m,
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String newPropertyVal
  ) {
    super(m);
    this.property = property;
    this.eventSubject = eventSubject;
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.toDateStringTtimeString = toDateStringTtimeString;
    this.newPropertyVal = newPropertyVal;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^edit event (.*?) (\\S+) from (\\S+) to (\\S+) with (\\S+)"
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
    String toDateTime = matcher.group(4);
    String newVal = matcher.group(5);
    new EditSingleEvent(
            r,
            property,
            eventSubject,
            fromDateTime,
            toDateTime,
            newVal
    ).perform();
    out.append(
            String.format(
                    "Edited %s of \"%s\" from %s to %s%s",
                    property,
                    eventSubject,
                    fromDateTime,
                    toDateTime,
                    System.lineSeparator()
            )
    );
  }

  @Override
  public void perform() {
    m.editSingleEvent(
            property,
            eventSubject,
            fromDateStringTtimeString,
            toDateStringTtimeString,
            newPropertyVal
    );
  }
}
