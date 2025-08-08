package model.calendar;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.events.CalEventImpl;
import model.events.RobustCalEvent;
import model.events.ZonedCalEventImpl;

/**
 * An implementation of a robust calendar that offers not only the option
 * to create events but also to edit them, as well as the option to query
 * this calendar.
 */
public class CalendarImpl implements RobustCalendar {
  private final Set<RobustCalEvent> events;
  private String name;
  private ZoneId zone;

  /**
   * Construct a new calendar object with empty event and event series sets
   * and no time zone.
   */
  public CalendarImpl() {
    this(null, null);
  }

  /**
   * Construct a new calendar object with empty event and event series sets
   * and an immutable time zone.
   *
   * @param zone a provided time zone.
   */
  public CalendarImpl(String name, String zone) {
    events = new TreeSet<>();
    this.zone = (zone != null) ? ZoneId.of(zone) : null;
    this.name = name;
  }

  @Override
  public Set<RobustCalEvent> getEvents() {
    return events;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getZone() {
    return zone.toString();
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setZone(String zone) {
    this.zone = (zone != null) ? ZoneId.of(zone) : null;
  }

  @Override
  public void createSingleNormalEvent(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  ) {
    events.add(
            createOneNormalEvent(
                    eventSubject,
                    fromDateStringTtimeString,
                    toDateStringTtimeString
            )
    );
  }

  private RobustCalEvent createOneNormalEvent(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  ) {
    return new CalEventImpl(
            eventSubject,
            LocalDateTime.parse(fromDateStringTtimeString),
            LocalDateTime.parse(toDateStringTtimeString)
    );
  }

  @Override
  public void createRecurringNormalEventsNTimes(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String repeatWeekdays,
          int N
  ) {
    // if N is 0 or 1, do one event creation only
    if (N == 0 || N == 1) {
      createSingleNormalEvent(eventSubject, fromDateStringTtimeString, toDateStringTtimeString);
    }
    // define the new series
    RobustCalEvent.Series newSeries = new RobustCalEvent.Series();
    // get starting data
    if (LocalDateTime.parse(fromDateStringTtimeString).toLocalDate().getDayOfMonth() !=
            LocalDateTime.parse(toDateStringTtimeString).toLocalDate().getDayOfMonth()
    ) {
      throw new DateTimeException("Cannot repeat an event that doesn't end on " +
              "the same day it starts on!");
    }
    RobustCalEvent firstEvent = createOneNormalEvent(
            eventSubject, fromDateStringTtimeString, toDateStringTtimeString
    );
    firstEvent.setSeries(newSeries);
    newSeries.add(firstEvent);
    // retrieve the weekdays
    for (char weekday : repeatWeekdays.toCharArray()) {
      if (!CalEventImpl.getDaysOfTheWeek().containsKey(weekday)) {
        throw new DateTimeException("Invalid weekday letters entered! " +
                "'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday, " +
                "'F' is Friday, 'S' is Saturday, and 'U' is Sunday."
        );
      }
    }
    /* cases for weekdays:
     *  1. weekdays are after the current one, in which case in the first
     *     iteration we can continue as wanted
     *  2. weekdays are BEFORE the current one, in which case in the first
     *     iteration we can continue with the OTHER weekdays, BUT ADD them
     *     from the NEXT iteration onward */
    // create a wrapper class version of the repeated weekdays
    Character[] weekdays = new Character[repeatWeekdays.length()];
    for (int i = 0; i < repeatWeekdays.length(); i++) {
      weekdays[i] = repeatWeekdays.toCharArray()[i];
    }
    // store the days of the week
    DayOfWeek[] repeatDaysOfWeek = Arrays.stream(weekdays)
            .map(CalEventImpl.getDaysOfTheWeek()::get)
            .toArray(DayOfWeek[]::new);
    // find the offset from the day of the week of the first day
    int firstEventDayOfWeekNum = firstEvent
            .getStartDateTime().toLocalDate().getDayOfWeek().ordinal();
    Integer[] dayOfWeekOffset = Arrays.stream(repeatDaysOfWeek)
            .map((it) -> it.ordinal() - firstEventDayOfWeekNum)
            .toArray(Integer[]::new);
    /* plan:
     *  1. find the first iteration of the dates, keeping in mind the two cases
     *     defined above
     *     a. if they're after, just add the days and create the date
     *     b. if they're before, add 7 to the ordinal difference and create that
     *        event, that many days after
     *  2. then, for a total of N times, create days that mirror these events on
     *     the specified days of the week, adding seven days to the date */
    // define an ordered collection of all the events
    Set<RobustCalEvent> allFirstEvents = new TreeSet<>();
    // define auxiliary before and after events data structures
    List<RobustCalEvent> beforeFirstEvents = new ArrayList<>();
    List<RobustCalEvent> afterFirstEvents = new ArrayList<>();
    // define data structures of the non-0 offsets for repeating events on certain days
    Integer[] beforeOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it < 0)
            .map((it) -> it + 7)
            .toArray(Integer[]::new);
    Integer[] afterOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it >= 0)
            .toArray(Integer[]::new);
    // create these relevant repeating events' first iterations
    createWeekdayRepeatNormalEvents(eventSubject, firstEvent, beforeFirstEvents, beforeOffsets);
    createWeekdayRepeatNormalEvents(eventSubject, firstEvent, afterFirstEvents, afterOffsets);
    // add these first iterations to the set of all first events
    allFirstEvents.addAll(beforeFirstEvents);
    allFirstEvents.addAll(afterFirstEvents);
    // now, create the new events to add a total of N times
    RobustCalEvent[] allFirstEventsArray = allFirstEvents.toArray(RobustCalEvent[]::new);
    int i = 0;
    while (newSeries.getEvents().size() < N) {
      for (RobustCalEvent event : allFirstEventsArray) {
        RobustCalEvent newEvent = new CalEventImpl(
                eventSubject,
                LocalDateTime.of(
                        LocalDate.ofEpochDay(
                                event.getStartDateTime().toLocalDate().toEpochDay()
                                        + 7L * i
                        ), event.getStartDateTime().toLocalTime()
                ),
                LocalDateTime.of(
                        LocalDate.ofEpochDay(
                                event.getEndDateTime().toLocalDate().toEpochDay()
                                        + 7L * i
                        ), event.getEndDateTime().toLocalTime()
                )
        );
        if (newSeries.getEvents().size() < N) {
          newEvent.setSeries(newSeries);
          newSeries.add(newEvent);
        } else {
          break;
        }
      }
      i++;
    }
    // finally, store all the events in the calendar's events field
    events.addAll(newSeries.getEvents());
  }

  @Override
  public void createRecurringNormalEventsUntilDate(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String repeatWeekdays,
          String untilDateString
  ) {
    // calculate an undershot (for safety) amount of weeks to repeat the event
    LocalDate untilDate = LocalDate.parse(untilDateString);
    int times = Math.toIntExact(
            (int) (
                    untilDate.toEpochDay()
                            - LocalDateTime.parse(fromDateStringTtimeString)
                            .toLocalDate().toEpochDay()
            ) / 7
    );
    // define the new series
    RobustCalEvent.Series newSeries = new RobustCalEvent.Series();
    // get starting data
    if (LocalDateTime.parse(fromDateStringTtimeString).toLocalDate().getDayOfMonth() !=
            LocalDateTime.parse(toDateStringTtimeString).toLocalDate().getDayOfMonth()
    ) {
      throw new DateTimeException("Cannot repeat an event that doesn't end on " +
              "the same day it starts on!");
    }
    RobustCalEvent firstEvent = createOneNormalEvent(
            eventSubject, fromDateStringTtimeString, toDateStringTtimeString
    );
    firstEvent.setSeries(newSeries);
    newSeries.add(firstEvent);
    // retrieve the weekdays
    for (char weekday : repeatWeekdays.toCharArray()) {
      if (!CalEventImpl.getDaysOfTheWeek().containsKey(weekday)) {
        throw new DateTimeException("Invalid weekday letters entered!");
      }
    }
    /* cases for weekdays:
     *  1. weekdays are after the current one, in which case in the first
     *     iteration we can continue as wanted
     *  2. weekdays are BEFORE the current one, in which case in the first
     *     iteration we can continue with the OTHER weekdays, BUT ADD them
     *     from the NEXT iteration onward */
    // create a wrapper class version of the repeated weekdays
    Character[] weekdays = new Character[repeatWeekdays.length()];
    for (int i = 0; i < repeatWeekdays.length(); i++) {
      weekdays[i] = repeatWeekdays.toCharArray()[i];
    }
    // store the days of the week
    DayOfWeek[] repeatDaysOfWeek = Arrays.stream(weekdays)
            .map(CalEventImpl.getDaysOfTheWeek()::get)
            .toArray(DayOfWeek[]::new);
    // find the offset from the day of the week of the first day
    int firstEventDayOfWeekNum = firstEvent
            .getStartDateTime().toLocalDate().getDayOfWeek().ordinal();
    Integer[] dayOfWeekOffset = Arrays.stream(repeatDaysOfWeek)
            .map((it) -> it.ordinal() - firstEventDayOfWeekNum)
            .toArray(Integer[]::new);
    /* plan:
     *  1. find the first iteration of the dates, keeping in mind the two cases
     *     defined above
     *     a. if they're after, just add the days and create the date
     *     b. if they're before, add 7 to the ordinal difference and create that
     *        event, that many days after
     *  2. then, for a total of N times, create days that mirror these events on
     *     the specified days of the week, adding seven days to the date */
    // define an ordered collection of all the events
    Set<RobustCalEvent> allFirstEvents = new TreeSet<>();
    // define auxiliary before and after events data structures
    List<RobustCalEvent> beforeFirstEvents = new ArrayList<>();
    List<RobustCalEvent> afterFirstEvents = new ArrayList<>();
    // define data structures of the non-0 offsets for repeating events on certain days
    Integer[] beforeOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it < 0)
            .map((it) -> it + 7)
            .toArray(Integer[]::new);
    Integer[] afterOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it >= 0)
            .toArray(Integer[]::new);
    // create these relevant repeating events' first iterations
    createWeekdayRepeatNormalEvents(eventSubject, firstEvent, beforeFirstEvents, beforeOffsets);
    createWeekdayRepeatNormalEvents(eventSubject, firstEvent, afterFirstEvents, afterOffsets);
    // add these first iterations to the set of all first events
    allFirstEvents.addAll(beforeFirstEvents);
    allFirstEvents.addAll(afterFirstEvents);
    // add these first iterations of the new events to the series
    if (times <= 1) {
      for (RobustCalEvent event : allFirstEvents) {
        if (event.getStartDateTime().toLocalDate().isBefore(untilDate)) {
          event.setSeries(newSeries);
          newSeries.add(event);
        } else {
          break;
        }
      }
      // quit preliminarily again if N is near 1
      return;
    } else {
      newSeries.addAll(allFirstEvents);
    }
    // now, create the new events to add a total of N times
    for (RobustCalEvent event : allFirstEvents) {
      for (int i = 1; i <= times; i++) {
        RobustCalEvent newEvent = new CalEventImpl(
                eventSubject,
                LocalDateTime.of(
                        LocalDate.ofEpochDay(
                                event.getStartDateTime().toLocalDate().toEpochDay()
                                        + 7L * i
                        ), event.getStartDateTime().toLocalTime()
                ),
                LocalDateTime.of(
                        LocalDate.ofEpochDay(
                                event.getEndDateTime().toLocalDate().toEpochDay()
                                        + 7L * i
                        ), event.getEndDateTime().toLocalTime()
                )
        );
        if (i == times) {
          if (newEvent.getStartDateTime().toLocalDate().isBefore(untilDate)) {
            newEvent.setSeries(newSeries);
            newSeries.add(newEvent);
          }
        } else {
          newEvent.setSeries(newSeries);
          newSeries.add(newEvent);
        }
      }
    }
    // finally, store all the events in the calendar's events field
    events.addAll(newSeries.getEvents());
  }

  private void createWeekdayRepeatNormalEvents(
          String eventSubject,
          RobustCalEvent firstEvent,
          List<RobustCalEvent> firstEvents,
          Integer[] offsets
  ) {
    for (int offset : offsets) {
      RobustCalEvent newEvent = new CalEventImpl(
              eventSubject,
              LocalDateTime.of(
                      LocalDate.ofEpochDay(
                              firstEvent.getStartDateTime().toLocalDate().toEpochDay()
                                      + offset
                      ), firstEvent.getStartDateTime().toLocalTime()
              ),
              LocalDateTime.of(
                      LocalDate.ofEpochDay(
                              firstEvent.getEndDateTime().toLocalDate().toEpochDay()
                                      + offset
                      ), firstEvent.getEndDateTime().toLocalTime()
              )
      );
      firstEvents.add(newEvent);
    }
  }

  @Override
  public void createSingleAllDayEvent(String eventSubject, String dateString) {
    events.add(createOneAllDayEvent(eventSubject, dateString));
  }

  private RobustCalEvent createOneAllDayEvent(String eventSubject, String dateString) {
    return new CalEventImpl(eventSubject, LocalDate.parse(dateString));
  }

  @Override
  public void createRecurringAllDayEventsNTimes(
          String eventSubject,
          String dateString,
          String repeatWeekdays,
          int N
  ) {
    // if N is 0 or 1, do one event creation only
    if (N == 0 || N == 1) {
      createSingleAllDayEvent(eventSubject, dateString);
    }
    // define the new series
    RobustCalEvent.Series newSeries = new RobustCalEvent.Series();
    // get starting data
    RobustCalEvent firstEvent = createOneAllDayEvent(eventSubject, dateString);
    firstEvent.setSeries(newSeries);
    newSeries.add(firstEvent);
    // retrieve the weekdays
    for (char weekday : repeatWeekdays.toCharArray()) {
      if (!CalEventImpl.getDaysOfTheWeek().containsKey(weekday)) {
        throw new DateTimeException("Invalid weekday letters entered!");
      }
    }
    /* cases for weekdays:
     *  1. weekdays are after the current one, in which case in the first
     *     iteration we can continue as wanted
     *  2. weekdays are BEFORE the current one, in which case in the first
     *     iteration we can continue with the OTHER weekdays, BUT ADD them
     *     from the NEXT iteration onward */
    // create a wrapper class version of the repeated weekdays
    Character[] weekdays = new Character[repeatWeekdays.length()];
    for (int i = 0; i < repeatWeekdays.length(); i++) {
      weekdays[i] = repeatWeekdays.toCharArray()[i];
    }
    // store the days of the week
    DayOfWeek[] repeatDaysOfWeek = Arrays.stream(weekdays)
            .map(CalEventImpl.getDaysOfTheWeek()::get)
            .toArray(DayOfWeek[]::new);
    // find the offset from the day of the week of the first day
    int firstEventDayOfWeekNum = firstEvent
            .getStartDateTime().toLocalDate().getDayOfWeek().ordinal();
    Integer[] dayOfWeekOffset = Arrays.stream(repeatDaysOfWeek)
            .map((it) -> it.ordinal() - firstEventDayOfWeekNum)
            .toArray(Integer[]::new);
    /* plan:
     *  1. find the first iteration of the dates, keeping in mind the two cases
     *     defined above
     *     a. if they're after, just add the days and create the date
     *     b. if they're before, add 7 to the ordinal difference and create that
     *        event, that many days after
     *  2. then, for a total of N times, create days that mirror these events on
     *     the specified days of the week, adding seven days to the date */
    // define an ordered collection of all the events
    Set<RobustCalEvent> allFirstEvents = new TreeSet<>();
    // define auxiliary before and after events data structures
    List<RobustCalEvent> beforeFirstEvents = new ArrayList<>();
    List<RobustCalEvent> afterFirstEvents = new ArrayList<>();
    // define data structures of the non-0 offsets for repeating events on certain days
    Integer[] beforeOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it < 0)
            .map((it) -> it + 7)
            .toArray(Integer[]::new);
    Integer[] afterOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it >= 0)
            .toArray(Integer[]::new);
    // create these relevant repeating events' first iterations
    createWeekdayRepeatNormalEvents(eventSubject, firstEvent, beforeFirstEvents, beforeOffsets);
    createWeekdayRepeatNormalEvents(eventSubject, firstEvent, afterFirstEvents, afterOffsets);
    // add these first iterations to the set of all first events
    allFirstEvents.addAll(beforeFirstEvents);
    allFirstEvents.addAll(afterFirstEvents);
    // now, create the new events to add a total of N times
    RobustCalEvent[] allFirstEventsArray = allFirstEvents.toArray(RobustCalEvent[]::new);
    int i = 0;
    while (newSeries.getEvents().size() < N) {
      for (RobustCalEvent event : allFirstEventsArray) {
        RobustCalEvent newEvent = new CalEventImpl(
                eventSubject,
                LocalDate.ofEpochDay(
                        event.getStartDateTime().toLocalDate().toEpochDay()
                                + 7L * i
                )
        );
        if (newSeries.getEvents().size() < N) {
          newEvent.setSeries(newSeries);
          newSeries.add(newEvent);
        } else {
          break;
        }
      }
      i++;
    }
    // finally, store all the events in the calendar's events field
    events.addAll(newSeries.getEvents());
  }

  @Override
  public void createRecurringAllDayEventsUntilDate(
          String eventSubject,
          String dateString,
          String repeatWeekdays,
          String untilDateString
  ) {
    // calculate undershot (for safety) amount of weeks to repeat the event
    LocalDate untilDate = LocalDate.parse(untilDateString);
    int times = Math.toIntExact(
            (int) (
                    untilDate.toEpochDay() - LocalDate.parse(dateString).toEpochDay()
            ) / 7
    );
    // define the new series
    RobustCalEvent.Series newSeries = new RobustCalEvent.Series();
    // get starting data
    RobustCalEvent firstEvent = createOneAllDayEvent(eventSubject, dateString);
    firstEvent.setSeries(newSeries);
    newSeries.add(firstEvent);
    // retrieve the weekdays
    for (char weekday : repeatWeekdays.toCharArray()) {
      if (!CalEventImpl.getDaysOfTheWeek().containsKey(weekday)) {
        throw new DateTimeException("Invalid weekday letters entered!");
      }
    }
    /* cases for weekdays:
     *  1. weekdays are after the current one, in which case in the first
     *     iteration we can continue as wanted
     *  2. weekdays are BEFORE the current one, in which case in the first
     *     iteration we can continue with the OTHER weekdays, BUT ADD them
     *     from the NEXT iteration onward */
    // create a wrapper class version of the repeated weekdays
    Character[] weekdays = new Character[repeatWeekdays.length()];
    for (int i = 0; i < repeatWeekdays.length(); i++) {
      weekdays[i] = repeatWeekdays.toCharArray()[i];
    }
    // store the days of the week
    DayOfWeek[] repeatDaysOfWeek = Arrays.stream(weekdays)
            .map(CalEventImpl.getDaysOfTheWeek()::get)
            .toArray(DayOfWeek[]::new);
    // find the offset from the day of the week of the first day
    int firstEventDayOfWeekNum = firstEvent
            .getStartDateTime().toLocalDate().getDayOfWeek().ordinal();
    Integer[] dayOfWeekOffset = Arrays.stream(repeatDaysOfWeek)
            .map((it) -> it.ordinal() - firstEventDayOfWeekNum)
            .toArray(Integer[]::new);
    /* plan:
     *  1. find the first iteration of the dates, keeping in mind the two cases
     *     defined above
     *     a. if they're after, just add the days and create the date
     *     b. if they're before, add 7 to the ordinal difference and create that
     *        event, that many days after
     *  2. then, for a total of N times, create days that mirror these events on
     *     the specified days of the week, adding seven days to the date */
    // define an ordered collection of all the events
    Set<RobustCalEvent> allFirstEvents = new TreeSet<>();
    // define auxiliary before and after events data structures
    List<RobustCalEvent> beforeFirstEvents = new ArrayList<>();
    List<RobustCalEvent> afterFirstEvents = new ArrayList<>();
    // define data structures of the non-0 offsets for repeating events on certain days
    Integer[] beforeOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it < 0)
            .map((it) -> it + 7)
            .toArray(Integer[]::new);
    Integer[] afterOffsets = Arrays.stream(dayOfWeekOffset)
            .filter((it) -> it >= 0)
            .toArray(Integer[]::new);
    // create these relevant repeating events' first iterations
    createWeekdayRepeatAllDayEvents(eventSubject, firstEvent, beforeFirstEvents, beforeOffsets);
    createWeekdayRepeatAllDayEvents(eventSubject, firstEvent, afterFirstEvents, afterOffsets);
    // add these first iterations to the set of all first events
    allFirstEvents.addAll(beforeFirstEvents);
    allFirstEvents.addAll(afterFirstEvents);
    // add these first iterations of the new events to the series
    if (times <= 1) {
      for (RobustCalEvent event : allFirstEvents) {
        if (event.getStartDateTime().toLocalDate().isBefore(untilDate)) {
          event.setSeries(newSeries);
          newSeries.add(event);
        } else {
          break;
        }
      }
      // quit preliminarily again if N is near 1
      return;
    } else {
      newSeries.addAll(allFirstEvents);
    }
    // now, create the new events to add a total of N times
    for (RobustCalEvent event : allFirstEvents) {
      for (int i = 1; i <= times; i++) {
        RobustCalEvent newEvent = new CalEventImpl(
                eventSubject,
                LocalDate.ofEpochDay(
                        event.getStartDateTime().toLocalDate().toEpochDay()
                                + 7L * i
                )
        );
        if (i == times) {
          if (newEvent.getStartDateTime().toLocalDate().isBefore(untilDate)) {
            event.setSeries(newSeries);
            newSeries.add(newEvent);
          }
        } else {
          event.setSeries(newSeries);
          newSeries.add(newEvent);
        }
      }
    }
    // finally, store all the events in the calendar's events field
    events.addAll(newSeries.getEvents());
  }

  private void createWeekdayRepeatAllDayEvents(
          String eventSubject,
          RobustCalEvent firstEvent,
          List<RobustCalEvent> firstEvents,
          Integer[] offsets
  ) {
    for (int offset : offsets) {
      RobustCalEvent newEvent = new CalEventImpl(
              eventSubject,
              LocalDate.ofEpochDay(
                      firstEvent.getStartDateTime().toLocalDate().toEpochDay()
                              + offset
              )
      );
      firstEvents.add(newEvent);
    }
  }

  @Override
  public void editSingleEvent(
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String newPropertyVal
  ) {
    // ensure the event set isn't empty
    if (events.isEmpty()) {
      throw new IllegalStateException("No events to pick from to edit!");
    }
    // find the target event, if any
    RobustCalEvent targetEvent = events.stream()
            .filter(
                    (it) ->
                            it.getSubject().equals(eventSubject)
                                    && it.getStartDateTime().equals(
                                    LocalDateTime.parse(fromDateStringTtimeString)
                            )
                                    && it.getEndDateTime().equals(
                                    LocalDateTime.parse(toDateStringTtimeString)
                            )
            )
            .findFirst()
            .orElse(null);
    // ensure the target event is not null, then do the corresponding action
    if (targetEvent != null) {
      switch (property) {
        case "subject" -> targetEvent.setSubject(newPropertyVal);
        case "start" -> {
          targetEvent.setStartDateTime(newPropertyVal);
          if (targetEvent.getSeries() != null) {
            targetEvent.getSeries().remove(targetEvent);
            targetEvent.setSeries(null);
          }
        }
        case "end" -> {
          targetEvent.setEndDateTime(newPropertyVal);
          if (targetEvent.getSeries() != null) {
            targetEvent.getSeries().remove(targetEvent);
            targetEvent.setSeries(null);
          }
        }
        case "description" -> targetEvent.setDescription(newPropertyVal);
        case "location" -> targetEvent.setLocation(newPropertyVal);
        case "status" -> targetEvent.setStatus(newPropertyVal);
        // if the specified property doesn't exist, throw relevant exception
        default -> throw new IllegalArgumentException("No properties match the one passed!");
      }
      // if no event was found, throw relevant exception
    } else {
      throw new IllegalArgumentException("No events match the provided subject and/or times!");
    }
  }

  @Override
  public void editEventAndMaybeOnward(
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String newPropertyVal
  ) {
    // ensure the event set isn't empty
    if (events.isEmpty()) {
      throw new IllegalStateException("No events to pick from to edit!");
    }
    // find the target event, if any
    RobustCalEvent targetEvent = events.stream()
            .filter(
                    (it) ->
                            it.getSubject().equals(eventSubject)
                                    && it.getStartDateTime().equals(
                                    LocalDateTime.parse(fromDateStringTtimeString)
                            )
            )
            .findFirst()
            .orElse(null);
    // ensure the target event is not null, then do the corresponding action
    if (targetEvent != null) {
      switch (property) {
        case "subject" -> targetEvent.setSubject(newPropertyVal);
        case "start" -> {
          targetEvent.setStartDateTime(newPropertyVal);
          if (targetEvent.getSeries() != null) {
            targetEvent.getSeries().remove(targetEvent);
            targetEvent.setSeries(null);
          }
        }
        case "end" -> {
          targetEvent.setEndDateTime(newPropertyVal);
          if (targetEvent.getSeries() != null) {
            targetEvent.getSeries().remove(targetEvent);
            targetEvent.setSeries(null);
          }
        }
        case "description" -> targetEvent.setDescription(newPropertyVal);
        case "location" -> targetEvent.setLocation(newPropertyVal);
        case "status" -> targetEvent.setStatus(newPropertyVal);
        // if the specified property doesn't exist, throw relevant exception
        default -> throw new IllegalArgumentException("No properties match the one passed!");
      }
      // if no event was found, throw relevant exception
    } else {
      throw new IllegalArgumentException("No events match the provided subject and/or times!");
    }
    // find whether the current event is in a series
    if (targetEvent.getSeries() != null) {
      // if so, create a new series and separate from the old one, if property was start/end
      RobustCalEvent.Series newSeries = null;
      if (property.equals("start") || property.equals("end")) {
        newSeries = new RobustCalEvent.Series();
        targetEvent.setSeries(newSeries);
        newSeries.add(targetEvent);
      }
      // store the target series as an iterable list
      List<RobustCalEvent> targetSeries =
              new ArrayList<>(targetEvent.getSeries().getEvents().stream().toList());
      // find the index of the target event, then only iterate from there on out
      final int startingIndex = targetSeries.indexOf(targetEvent);
      for (int i = startingIndex + 1; i < targetSeries.size(); i++) {
        RobustCalEvent currentEvent = targetSeries.get(i);
        String currentSubject = currentEvent.getSubject();
        String currentStart = currentEvent.getStartDateTime().toString();
        String currentEnd = currentEvent.getEndDateTime().toString();
        editSingleEvent(property, currentSubject, currentStart, currentEnd, newPropertyVal);
        // again, ensure that new series actions are only carried out when property is start/end
        if (property.equals("start") || property.equals("end")) {
          currentEvent.setSeries(newSeries);
          newSeries.add(currentEvent);
        }
      }
    }
  }

  @Override
  public void editEventAndMaybeAll(
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String newPropertyVal
  ) {
    // ensure the event set isn't empty
    if (events.isEmpty()) {
      throw new IllegalStateException("No events to pick from to edit!");
    }
    // find the target event, if any
    RobustCalEvent targetEvent = events.stream()
            .filter(
                    (it) ->
                            it.getSubject().equals(eventSubject)
                                    && it.getStartDateTime().equals(
                                    LocalDateTime.parse(fromDateStringTtimeString)
                            )
            )
            .findFirst()
            .orElse(null);
    /* carry out series editing first; no new series needed as that'd be redundant
     * when all elements in this series would be edited and added to another one which
     * still contains the same events as before, just edited */
    if (targetEvent != null) {
      if (targetEvent.getSeries() != null) {
        for (RobustCalEvent event : targetEvent.getSeries().getEvents()) {
          switch (property) {
            case "subject" -> event.setSubject(newPropertyVal);
            case "start" -> event.setStartDateTime(newPropertyVal);
            case "end" -> event.setEndDateTime(newPropertyVal);
            case "description" -> event.setDescription(newPropertyVal);
            case "location" -> event.setLocation(newPropertyVal);
            case "status" -> event.setStatus(newPropertyVal);
            // if the specified property doesn't exist, throw relevant exception
            default -> throw new IllegalArgumentException("No properties match the one passed!");
          }
        }
        // if not part of a series, simply do single event editing
      } else {
        editSingleEvent(
                property,
                eventSubject,
                fromDateStringTtimeString,
                targetEvent.getEndDateTime().toString(),
                newPropertyVal
        );
      }
    }
  }

  @Override
  public List<RobustCalEvent> printAllEventsOnDate(String dateString) {
    // create a list of the events
    List<RobustCalEvent> events = new ArrayList<>();
    // retrieve the start and end date times of the date
    LocalDateTime startDateTime = LocalDateTime.of(
            LocalDate.parse(dateString),
            LocalTime.of(LocalTime.MIN.getHour(), LocalTime.MIN.getMinute())
    );
    LocalDateTime endDateTime = LocalDateTime.of(
            LocalDate.parse(dateString),
            LocalTime.of(LocalTime.MAX.getHour(), LocalTime.MAX.getMinute())
    );
    // iterate over the event set
    eventPrintingHelper(startDateTime, endDateTime, events);
    // return the events satisfying the conditions
    return events;
  }

  @Override
  public List<RobustCalEvent> printAllEventsBetweenDates(
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  ) {
    // create a list of the events
    List<RobustCalEvent> events = new ArrayList<>();
    // retrieve the start and end date times of the date
    LocalDateTime startDateTime = LocalDateTime.parse(
            fromDateStringTtimeString
    );
    LocalDateTime endDateTime = LocalDateTime.parse(
            toDateStringTtimeString
    );
    // iterate over the event set
    eventPrintingHelper(startDateTime, endDateTime, events);
    return events;
  }

  private void eventPrintingHelper(
          LocalDateTime startDateTime,
          LocalDateTime endDateTime,
          List<RobustCalEvent> events
  ) {
    for (RobustCalEvent event : getEvents()) {
      // retrieve the event's starting and ending dates and times
      LocalDateTime eventStartDateTime = event.getStartDateTime();
      LocalDateTime eventEndDateTime = event.getEndDateTime();
      /* condition checks separated for proper ability to check */
      // first condition: start before but end during day
      boolean condition1 = eventEndDateTime.isAfter(startDateTime) &&
              eventEndDateTime.isBefore(endDateTime);
      // second condition: start and end within day itself
      boolean condition2 = eventStartDateTime.isAfter(startDateTime) &&
              eventEndDateTime.isBefore(endDateTime);
      // third condition: start during but end after day
      boolean condition3 = eventStartDateTime.isAfter(startDateTime) &&
              eventStartDateTime.isBefore(endDateTime);
      // put conditions all together
      if (condition1 || condition2 || condition3) {
        events.add(new ZonedCalEventImpl(event, zone));
      }
    }
  }

  @Override
  public String showStatusOnDateAtTime(String dateStringTtimeString) {
    LocalDateTime targetTime = LocalDateTime.parse(dateStringTtimeString);
    for (RobustCalEvent event : getEvents()) {
      // retrieve the event's starting and ending dates and times
      LocalDateTime startDateTime = event.getStartDateTime();
      LocalDateTime endDateTime = event.getEndDateTime();
      // check whether the target time is within an event's time
      if (targetTime.isAfter(startDateTime) &&
              targetTime.isBefore(endDateTime)
      ) {
        return "busy";
      }
    }
    return "available";
  }

  @Override
  public boolean equals(Object other) {
    return this == other
            || other instanceof RobustCalendar r
            && getName().equals(r.getName());
  }
}
