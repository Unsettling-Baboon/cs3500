package controller.commands.calendar;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.events.RobustCalEvent;
import model.calendar.RobustCalendar;

public class PrintAllEventsBetweenDates extends CalCommand {
  private List<RobustCalEvent> events;

  public PrintAllEventsBetweenDates(
          RobustCalendar m,
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  ) {
    super(m);
    this.fromDateStringTtimeString = fromDateStringTtimeString;
    this.toDateStringTtimeString = toDateStringTtimeString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^print events from (.*?) to (\\S+)$"
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
    String fromDate = matcher.group(1).trim();
    String toDate = matcher.group(2).trim();
    PrintAllEventsBetweenDates command = new PrintAllEventsBetweenDates(
            r,
            fromDate,
            toDate
    );
    command.perform();
    for (RobustCalEvent event : command.getEvents()) {
      out.append(event.toString()).append(System.lineSeparator());
    }
  }

  public List<RobustCalEvent> getEvents() {
    return events;
  }

  @Override
  public void perform() {
    events = m.printAllEventsBetweenDates(
            fromDateStringTtimeString,
            toDateStringTtimeString
    );
  }
}
