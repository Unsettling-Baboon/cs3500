package model.calendars;

import java.util.NoSuchElementException;
import java.util.Set;

import model.calendar.RobustCalendar;

/**
 * A mock implementation of RobustCalendars for testing purposes. This class
 * primarily logs method calls to a {@link StringBuilder}.
 */
public class MockCalendars implements RobustCalendars {
  private final StringBuilder log;

  /**
   * Construct a new mock model for the multi-calendar program.
   */
  public MockCalendars() {
    this.log = new StringBuilder();
  }

  /**
   * Returns the accumulated log of method calls.
   *
   * @return A string containing all logged interactions.
   */
  public String getLog() {
    return log.toString();
  }

  @Override
  public RobustCalendar getActiveCal() throws NoSuchElementException {
    log.append("getActiveCal() called.\n");
    throw new NoSuchElementException("Mock: getActiveCal() called, but no actual calendar is managed by this logging mock.");
  }

  @Override
  public void createCalendar(String name, String timeZone) {
    log.append(String.format("createCalendar(name='%s', timeZone='%s') called.\n", name, timeZone));
  }

  @Override
  public void useCalendar(String name) throws NoSuchElementException {
    log.append(String.format("useCalendar(name='%s') called.\n", name));
  }

  @Override
  public void editCalendar(String name, String property, String newPropertyVal)
          throws NoSuchElementException, IllegalArgumentException {
    log.append(String.format("editCalendar(name='%s', property='%s', newPropertyVal='%s') called.\n",
            name, property, newPropertyVal));
  }

  @Override
  public void copySingleEvent(
          String name,
          String fromDateStringTtimeString,
          String calName,
          String newFromDateStringTtimeString
  ) throws NoSuchElementException {
    log.append(String.format("copySingleEvent(name='%s', fromDate='%s', calName='%s', newFromDate='%s') called.\n",
            name, fromDateStringTtimeString, calName, newFromDateStringTtimeString));
  }

  @Override
  public void copyAllEventsOnDay(
          String onDateString,
          String calName,
          String toDateString
  ) throws NoSuchElementException {
    log.append(String.format("copyAllEventsOnDay(onDate='%s', calName='%s', toDate='%s') called.\n",
            onDateString, calName, toDateString));
  }

  @Override
  public void copyAllEventsInBetweenDates(
          String fromDateString,
          String toDateString,
          String calName,
          String newDateString
  ) throws NoSuchElementException {
    log.append(String.format("copyAllEventsInBetweenDates(fromDate='%s', toDate='%s', calName='%s', newDate='%s') called.\n",
            fromDateString, toDateString, calName, newDateString));
  }

  @Override
  public Set<RobustCalendar> getAllCalendars() {
    return Set.of();
  }
}