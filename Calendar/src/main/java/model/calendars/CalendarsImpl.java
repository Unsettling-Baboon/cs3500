package model.calendars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import model.calendar.CalendarImpl;
import model.calendar.RobustCalendar;
import model.events.RobustCalEvent;

public class CalendarsImpl implements RobustCalendars {
  private final Set<RobustCalendar> calendars;
  private RobustCalendar activeCal;

  public CalendarsImpl() {
    this.calendars = new HashSet<>();
  }

  @Override
  public Set<RobustCalendar> getAllCalendars() {
    return calendars;
  }

  @Override
  public RobustCalendar getActiveCal() throws NoSuchElementException {
    if (activeCal != null) {
      return activeCal;
    } else {
      throw new NoSuchElementException("No active calendar set!");
    }
  }

  @Override
  public void createCalendar(String name, String timeZone) {
    calendars.add(new CalendarImpl(name, timeZone));
  }

  @Override
  public void useCalendar(String name) throws NoSuchElementException {
    // ensure calendars not empty
    if (calendars.isEmpty()) {
      throw new NoSuchElementException("No calendars to choose from!");
    }
    // find the calendar with name, if any
    RobustCalendar activeCalMaybe = calendars.stream()
            .filter((it) -> it.getName().equals(name))
            .findFirst()
            .orElse(null);
    if (activeCalMaybe != null) {
      activeCal = activeCalMaybe;
    } else {
      // ensure exception thrown if no calendar found
      throw new NoSuchElementException("No such calendar!");
    }
  }

  @Override
  public void editCalendar(String name, String property, String newPropertyVal)
          throws NoSuchElementException {
    // ensure calendars not empty
    if (calendars.isEmpty()) {
      throw new NoSuchElementException("No calendars to choose from!");
    }
    // find target calendar with name, if any
    RobustCalendar targetCal = calendars.stream()
            .filter((it) -> it.getName().equals(name))
            .findFirst()
            .orElse(null);
    // ensure it's not null
    if (targetCal == null) {
      throw new NoSuchElementException("No calendar with the given name!");
    }
    // carry out action based on property name
    switch (property) {
      case "name" -> targetCal.setName(newPropertyVal);
      case "timezone" -> targetCal.setZone(newPropertyVal);
      default -> throw new IllegalArgumentException("No property with that name!");
    }
  }

  @Override
  public void copySingleEvent(
          String name,
          String fromDateStringTtimeString,
          String calName,
          String newFromDateStringTtimeString
  ) throws NoSuchElementException {
    // find the source event, if any
    RobustCalEvent targetEventMaybe = getActiveCal().getEvents().stream()
            .filter(
                    (it) ->
                            it.getSubject().equals(name)
                                    && it.getStartDateTime().equals(
                                    LocalDateTime.parse(fromDateStringTtimeString)
                            )
            )
            .findFirst()
            .orElse(null);
    // find the target calendar, if any
    RobustCalendar targetCalMaybe = calendars.stream()
            .filter((it) -> it.getName().equals(calName))
            .findFirst()
            .orElse(null);
    // cast-convert the arguments with "maybe" not in name anymore
    if (targetEventMaybe instanceof RobustCalEvent targetEvent
            && targetCalMaybe instanceof RobustCalendar targetCal) {
      LocalDateTime startNew = LocalDateTime.parse(newFromDateStringTtimeString);
      LocalDateTime endNew = startNew.plusMinutes(
              ChronoUnit.MINUTES.between(
                      targetEvent.getStartDateTime(),
                      targetEvent.getEndDateTime()
              )
      );
      if (!targetEvent.isAllDay()) {
        targetCal.createSingleNormalEvent(
                name,
                startNew.toString(),
                endNew.toString()
        );
      } else {
        targetCal.createSingleAllDayEvent(
                name,
                startNew.toLocalDate().toString()
        );
      }
      // throw relevant exceptions
    } else if (targetEventMaybe == null && targetCalMaybe == null) {
      throw new NoSuchElementException("No event found nor an active calendar!");
    } else if (targetEventMaybe == null) {
      throw new NoSuchElementException("No event with that name or date exists!");
    } else {
      throw new NoSuchElementException("No active calendar set!");
    }
  }

  @Override
  public void copyAllEventsOnDay(
          String onDateString,
          String calName,
          String toDateString
  ) throws NoSuchElementException {
    // retrieve the events
    List<RobustCalEvent> events = getActiveCal().printAllEventsOnDate(onDateString);
    // find the target calendar with that name, if any
    RobustCalendar targetCalMaybe = calendars.stream()
            .filter((it) -> it.getName().equals(calName))
            .findFirst()
            .orElse(null);
    // copy over the events
    if (targetCalMaybe != null) {
      // find the time difference in between the chosen day and new dates
      long timeDiff = ChronoUnit.DAYS.between(
              LocalDate.parse(onDateString),
              LocalDate.parse(toDateString)
      );
      events.forEach(
              (it) -> {
                if (!it.isAllDay()) {
                  targetCalMaybe.createSingleNormalEvent(
                          it.getSubject(),
                          it.getStartDateTime().plusDays(timeDiff).toString(),
                          it.getEndDateTime().plusDays(timeDiff).toString()
                  );
                } else {
                  targetCalMaybe.createSingleAllDayEvent(
                          it.getSubject(),
                          it.getStartDateTime().toLocalDate()
                                  .plusDays(timeDiff).toString()
                  );
                }
              }
      );
    } else {
      throw new NoSuchElementException("No such calendar with that name!");
    }
  }

  @Override
  public void copyAllEventsInBetweenDates(
          String fromDateString,
          String toDateString,
          String calName,
          String newDateString
  ) throws NoSuchElementException {
    // retrieve the events
    List<RobustCalEvent> events = getActiveCal().printAllEventsBetweenDates(
            LocalDateTime.of(LocalDate.parse(fromDateString), LocalTime.parse("00:00"))
                    .toString(),
            LocalDateTime.of(LocalDate.parse(toDateString), LocalTime.parse("23:59"))
                    .toString()
    );
    // find the target calendar with that name, if any
    RobustCalendar targetCalMaybe = calendars.stream()
            .filter((it) -> it.getName().equals(calName))
            .findFirst()
            .orElse(null);
    // copy over the events
    if (targetCalMaybe != null) {
      events.forEach(
              (it) -> {
                if (!it.isAllDay()) {
                  targetCalMaybe.createSingleNormalEvent(
                          it.getSubject(),
                          it.getStartDateTime().toString(),
                          it.getEndDateTime().toString()
                  );
                } else {
                  targetCalMaybe.createSingleAllDayEvent(
                          it.getSubject(),
                          it.getStartDateTime().toLocalDate().toString()
                  );
                }
              }
      );
    } else {
      throw new NoSuchElementException("No such calendar with that name!");
    }
  }
}
