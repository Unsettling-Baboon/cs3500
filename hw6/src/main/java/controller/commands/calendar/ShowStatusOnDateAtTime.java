package controller.commands.calendar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.calendar.RobustCalendar;

public class ShowStatusOnDateAtTime extends CalCommand {
  private String status;

  public ShowStatusOnDateAtTime(
          RobustCalendar m,
          String dateString
  ) {
    super(m);
    this.dateString = dateString;
  }

  public static Matcher matcher(String prompt) {
    Pattern pattern = Pattern.compile(
            "^show status on (.*?)$"
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
    String date = matcher.group(1).trim();
    ShowStatusOnDateAtTime command = new ShowStatusOnDateAtTime(
            r,
            date
    );
    command.perform();
    out.append(command.getStatus()).append(System.lineSeparator());
  }

  public String getStatus() {
    return status;
  }

  @Override
  public void perform() {
    status = m.showStatusOnDateAtTime(dateString);
  }
}
