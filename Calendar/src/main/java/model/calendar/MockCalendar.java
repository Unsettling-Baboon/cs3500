package model.calendar;

import java.util.List;
import java.util.Set;

import model.events.RobustCalEvent;

public class MockCalendar implements RobustCalendar {
  private final StringBuilder log;

  public MockCalendar() {
    log = new StringBuilder();
  }

  public String getLog() {
    return log.toString();
  }

  @Override
  public void createSingleNormalEvent(String eventSubject, String fromDateStringTtimeString, String toDateStringTtimeString) {
    log.append("createSingleNormalEvent(")
            .append(eventSubject).append(", ")
            .append(fromDateStringTtimeString).append(", ")
            .append(toDateStringTtimeString).append(")\n");
  }

  @Override
  public void createRecurringNormalEventsNTimes(String eventSubject, String fromDateStringTtimeString, String toDateStringTtimeString, String repeatWeekdays, int N) {
    log.append("createRecurringNormalEventsNTimes(")
            .append(eventSubject).append(", ")
            .append(fromDateStringTtimeString).append(", ")
            .append(toDateStringTtimeString).append(", ")
            .append(repeatWeekdays).append(", ")
            .append(N).append(")\n");
  }

  @Override
  public void createRecurringNormalEventsUntilDate(String eventSubject, String fromDateStringTtimeString, String toDateStringTtimeString, String repeatWeekdays, String untilDateString) {
    log.append("createRecurringNormalEventsUntilDate(")
            .append(eventSubject).append(", ")
            .append(fromDateStringTtimeString).append(", ")
            .append(toDateStringTtimeString).append(", ")
            .append(repeatWeekdays).append(", ")
            .append(untilDateString).append(")\n");
  }

  @Override
  public void createSingleAllDayEvent(String eventSubject, String dateString) {
    log.append("createSingleAllDayEvent(")
            .append(eventSubject).append(", ")
            .append(dateString).append(")\n");
  }

  @Override
  public void createRecurringAllDayEventsNTimes(String eventSubject, String dateString, String repeatWeekdays, int N) {
    log.append("createRecurringAllDayEventsNTimes(")
            .append(eventSubject).append(", ")
            .append(dateString).append(", ")
            .append(repeatWeekdays).append(", ")
            .append(N).append(")\n");
  }

  @Override
  public void createRecurringAllDayEventsUntilDate(String eventSubject, String dateString, String repeatWeekdays, String untilDateString) {
    log.append("createRecurringAllDayEventsUntilDate(")
            .append(eventSubject).append(", ")
            .append(dateString).append(", ")
            .append(repeatWeekdays).append(", ")
            .append(untilDateString).append(")\n");
  }

  @Override
  public void editSingleEvent(String property, String eventSubject, String fromDateStringTtimeString, String toDateStringTtimeString, String newPropertyVal) {
    log.append("editSingleEvent(")
            .append(property).append(", ")
            .append(eventSubject).append(", ")
            .append(fromDateStringTtimeString).append(", ")
            .append(toDateStringTtimeString).append(", ")
            .append(newPropertyVal).append(")\n");
  }

  @Override
  public void editEventAndMaybeOnward(String property, String eventSubject, String fromDateStringTtimeString, String newPropertyVal) {
    log.append("editEventAndMaybeOnward(")
            .append(property).append(", ")
            .append(eventSubject).append(", ")
            .append(fromDateStringTtimeString).append(", ")
            .append(newPropertyVal).append(")\n");
  }

  @Override
  public void editEventAndMaybeAll(String property, String eventSubject, String fromDateStringTtimeString, String newPropertyVal) {
    log.append("editEventAndMaybeAll(")
            .append(property).append(", ")
            .append(eventSubject).append(", ")
            .append(fromDateStringTtimeString).append(", ")
            .append(newPropertyVal).append(")\n");
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public String getZone() {
    return null;
  }

  @Override
  public Set<RobustCalEvent> getEvents() {
    log.append("getEvents()\n");
    // Return an empty, unmodifiable set for testing purposes
    return Set.of();
  }

  @Override
  public List<RobustCalEvent> printAllEventsOnDate(String dateString) {
    log.append("printAllEventsOnDate(").append(dateString).append(")\n");
    return List.of(); // Return empty list for this mock
  }

  @Override
  public List<RobustCalEvent> printAllEventsBetweenDates(String fromDateStringTtimeString, String toDateStringTtimeString) {
    log.append("printAllEventsBetweenDates(")
            .append(fromDateStringTtimeString).append(", ")
            .append(toDateStringTtimeString).append(")\n");
    return List.of(); // Return empty list for this mock
  }

  @Override
  public String showStatusOnDateAtTime(String dateStringTtimeString) {
    log.append("showStatusOnDateAtTime(").append(dateStringTtimeString).append(")\n");
    return "available"; // Return a predictable status for the mock
  }

  @Override
  public void setName(String name) {

  }

  @Override
  public void setZone(String zone) {

  }
}
