import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import controller.calendars.CalendarsControllerImpl;
import controller.gui.CalendarControllerGUIImpl;
import model.calendars.CalendarsImpl;
import view.CalendarViewImpl;

public class Main {
  public static void main(String[] args) {
    File commands;
    if (args.length > 0) {
      if (args.length > 1) {
        if (args[0].equals("--mode")) {
          if (args[1].equals("interactive")) {
            try {
              new CalendarsControllerImpl(
                      new InputStreamReader(System.in),
                      System.out,
                      new CalendarsImpl()
              ).go();
            } catch (IOException i) {
              throw new IllegalStateException(i.getMessage());
            }
          } else {
            if (args.length > 2) {
              if (args[1].equals("headless")) {
                commands = new File(args[2]);
                try {
                  new CalendarsControllerImpl(
                          new BufferedReader(new FileReader(commands)),
                          System.out,
                          new CalendarsImpl()
                  ).go();
                } catch (IOException i) {
                  throw new IllegalStateException(i.getMessage());
                }
              }
            }
          }
        }
      }
    } else {
      new CalendarControllerGUIImpl(
              new CalendarsImpl(),
              new CalendarViewImpl()
      ).start();
    }
  }
}
