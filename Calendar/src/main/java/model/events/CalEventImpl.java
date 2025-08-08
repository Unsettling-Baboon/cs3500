package model.events;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import model.calendar.CalendarImpl;

/**
 * A class representing a robust calendar event with the ability to
 * track any date and time, as well as numerous descriptive fields,
 * such as a description, location, and status.
 */
public class CalEventImpl implements RobustCalEvent {
  private final static Map<Character, DayOfWeek> DAYS_OF_THE_WEEK =
          new HashMap<>(
                  Map.of(
                          'M', DayOfWeek.MONDAY,
                          'T', DayOfWeek.TUESDAY,
                          'W', DayOfWeek.WEDNESDAY,
                          'R', DayOfWeek.THURSDAY,
                          'F', DayOfWeek.FRIDAY,
                          'S', DayOfWeek.SATURDAY,
                          'U', DayOfWeek.SUNDAY
                  )
          );
  protected String subject;
  protected LocalDateTime startDateTime;
  protected LocalDateTime endDateTime;
  protected boolean isAllDay;
  protected Series series;
  private String description;
  protected Location location;
  private Status status;

  /**
   * Create a new "normal" calendar event as defined in the
   * {@code createSingleNormalEvent()} method of the {@link CalendarImpl}
   * class. Its status is defaulted to private.
   *
   * @param subject       a subject String.
   * @param startDateTime the start and date time for this event.
   * @param endDateTime   the end date and time for this event.
   * @throws DateTimeException if the start date and time is after the end
   *                           date and time, or the end date and time is
   *                           before the start date and time.
   */
  public CalEventImpl(
          String subject,
          LocalDateTime startDateTime,
          LocalDateTime endDateTime
  ) throws DateTimeException {
    if (startDateTime.isAfter(endDateTime) || endDateTime.isBefore(startDateTime)) {
      throw new DateTimeException("Invalid date or time entered in creating event!");
    }
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.status = Status.PRIVATE;
  }

  /**
   * Create a new "normal" calendar event as defined in the
   * {@code createSingleAllDayEvent()} method of the {@link CalendarImpl}
   * class. Its status is defaulted to private.
   *
   * @param subject   the subject to give this event.
   * @param startDate the date this event should last all-day on.
   */
  public CalEventImpl(String subject, LocalDate startDate) {
    this.subject = subject;
    this.startDateTime = LocalDateTime.of(
            startDate,
            LocalTime.of(8, 0)
    );
    this.endDateTime = LocalDateTime.of(
            startDate,
            LocalTime.of(17, 0)
    );
    this.isAllDay = true;
    this.status = Status.PRIVATE;
  }

  /**
   * A no-arg constructor for the purpose of this class's only subclass.
   */
  protected CalEventImpl() {}

  /**
   * Retrieve the conversion from days of the week as letters to the actual
   * day of the week.
   *
   * @return the map from characters representing days of the week to the
   * corresponding day of the week as a {@link DayOfWeek} enum.
   */
  public static Map<Character, DayOfWeek> getDaysOfTheWeek() {
    return DAYS_OF_THE_WEEK;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  @Override
  public boolean isAllDay() {
    return isAllDay;
  }

  @Override
  public Series getSeries() {
    return series;
  }

  @Override
  public String getDescription() {
    return (description != null) ? description : "Description not found";
  }

  @Override
  public Location getLocation() {
    return location;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public void setSeries(Series series) {
    this.series = series;
  }

  @Override
  public void setAllDay() {
    if (!isAllDay) {
      isAllDay = true;
      this.startDateTime = LocalDateTime.of(
              startDateTime.toLocalDate(),
              LocalTime.of(8, 0)
      );
      this.endDateTime = LocalDateTime.of(
              startDateTime.toLocalDate(),
              LocalTime.of(17, 0)
      );
    }
  }

  @Override
  public void setStartDateTime(String startDateStringTtimeString) {
    LocalDateTime startDateTime = LocalDateTime.parse(startDateStringTtimeString);
    if (startDateTime.isAfter(endDateTime)) {
      throw new DateTimeException("Start date/time after end date/time!" +
              " Edit end date/time first.");
    } else if (isAllDay) {
      isAllDay = false;
    } else {
      this.startDateTime = startDateTime;
    }
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setEndDateTime(String endDateStringTtimeString) {
    LocalDateTime endDateTime = LocalDateTime.parse(endDateStringTtimeString);
    if (endDateTime.isBefore(startDateTime)) {
      throw new DateTimeException("End date/time before start date/time!" +
              " Edit start date/time first.");
    } else if (isAllDay) {
      isAllDay = false;
    } else {
      this.endDateTime = endDateTime;
    }
  }

  @Override
  public void setLocation(String location) {
    for (int i = 0; i < Location.values().length; i++) {
      Location loc = Location.values()[i];
      if (loc.getStringRepresentation().equals(location)) {
        this.location = loc;
        return;
      } else {
        if (i == Location.values().length - 1) {
          throw new NoSuchElementException("Location must either be \"physical\" " +
                  "or \"online\".");
        }
      }
    }
  }

  @Override
  public void setStatus(String status) {
    for (int i = 0; i < Status.values().length; i++) {
      Status stat = Status.values()[i];
      if (stat.getStringRepresentation().equals(status)) {
        this.status = stat;
        return;
      } else {
        if (i == Status.values().length - 1) {
          throw new NoSuchElementException("Status must either be \"public\" or " +
                  "\"private\".");
        }
      }
    }
  }

  @Override
  public String toString() {
    String locationFormat = (location != null)
            ? String.format("; %s", location.getStringRepresentation()) : "";
    return (!isAllDay)
            ? String.format(
            "\"%s\", from %s %s to %s %s%s",
            subject,
            startDateTime.getDayOfWeek(),
            startDateTime,
            endDateTime.getDayOfWeek(),
            endDateTime,
            locationFormat
    ) : String.format(
            "\"%s\", on %s %s%s",
            subject,
            startDateTime.getDayOfWeek(),
            startDateTime.toLocalDate(),
            locationFormat
    );
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else {
      if (other instanceof CalEventImpl c) {
        return subject.equals(c.subject)
                && isAllDay == c.isAllDay
                && startDateTime.equals(c.startDateTime)
                && endDateTime.equals(c.endDateTime);
      } else {
        return false;
      }
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, isAllDay, startDateTime, endDateTime);
  }

  @Override
  public int compareTo(CalEvent o) {
    if (o instanceof CalEventImpl c) {
      if (!startDateTime.isEqual(c.startDateTime)) {
        return startDateTime.compareTo(c.startDateTime);
      } else if (!endDateTime.isEqual(c.endDateTime)) {
        return endDateTime.compareTo(c.endDateTime);
      } else if (!subject.equals(c.subject)) {
        return subject.compareTo(c.subject);
      } else if (isAllDay && !c.isAllDay) {
        return -1;
      } else if (!isAllDay && c.isAllDay) {
        return 1;
      } else {
        return 0;
      }
    } else {
      throw new IllegalArgumentException("Cannot compare different subtypes of CalEvent.");
    }
  }

  public enum Location {
    PHYSICAL("physical"),
    ONLINE("online");

    private final String stringRepresentation;

    Location(String stringRepresentation) {
      this.stringRepresentation = stringRepresentation;
    }

    public String getStringRepresentation() {
      return stringRepresentation;
    }
  }

  public enum Status {
    PUBLIC("public"),
    PRIVATE("private");

    private final String stringRepresentation;

    Status(String stringRepresentation) {
      this.stringRepresentation = stringRepresentation;
    }

    public String getStringRepresentation() {
      return stringRepresentation;
    }
  }
}
