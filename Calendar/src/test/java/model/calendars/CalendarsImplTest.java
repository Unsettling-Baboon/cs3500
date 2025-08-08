package model.calendars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import model.events.RobustCalEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A JUnit 5 test class for the {@link CalendarsImpl} class.
 */
public class CalendarsImplTest {
  private CalendarsImpl calendarsModel;

  @BeforeEach
  void setUp() {
    calendarsModel = new CalendarsImpl();
  }

  @Test
  void testGetActiveCalNoActiveCalendar() {
    // verifies that getActiveCal throws NoSuchElementException if no calendar is active.
    assertThrows(NoSuchElementException.class, () -> calendarsModel.getActiveCal());
  }

  @Test
  void testCreateCalendar() {
    // ensures that a new calendar can be created and added to the collection.
    calendarsModel.createCalendar("Work", "America/New_York");
    calendarsModel.createCalendar("Personal", "Europe/London");

    // we can't directly check internal calendars set, but can try to use them
    assertDoesNotThrow(() -> calendarsModel.useCalendar("Work"));
    assertDoesNotThrow(() -> calendarsModel.useCalendar("Personal"));
    assertThrows(NoSuchElementException.class, () -> calendarsModel.useCalendar("NonExistent"));
  }

  @Test
  void testUseCalendarExistingCalendar() {
    // confirms that an existing calendar can be set as the active calendar.
    calendarsModel.createCalendar("Home", "Asia/Tokyo");
    calendarsModel.useCalendar("Home");
    assertEquals("Home", calendarsModel.getActiveCal().getName());
    assertEquals("Asia/Tokyo", calendarsModel.getActiveCal().getZone());
  }

  @Test
  void testUseCalendarNoCalendarsExist() {
    // checks that useCalendar throws NoSuchElementException when no calendars have been created.
    assertThrows(NoSuchElementException.class, () -> calendarsModel.useCalendar("AnyCal"));
  }

  @Test
  void testUseCalendarNonExistentCalendar() {
    // validates that useCalendar throws NoSuchElementException for a non-existent calendar.
    calendarsModel.createCalendar("Study", "America/Chicago");
    assertThrows(NoSuchElementException.class, () -> calendarsModel.useCalendar("Random"));
  }

  @Test
  void testEditCalendarName() {
    // verifies that a calendar's name can be successfully edited.
    calendarsModel.createCalendar("OldName", "UTC");
    calendarsModel.editCalendar("OldName", "name", "NewName");
    assertThrows(NoSuchElementException.class, () -> calendarsModel.useCalendar("OldName")); // Old name should not exist
    assertDoesNotThrow(() -> calendarsModel.useCalendar("NewName")); // New name should exist
  }

  @Test
  void testEditCalendarTimeZone() {
    // ensures that a calendar's timezone can be updated.
    calendarsModel.createCalendar("Vacation", "Europe/Paris");
    calendarsModel.editCalendar("Vacation", "timezone", "America/Denver");
    calendarsModel.useCalendar("Vacation"); // set it active to retrieve its properties
    assertEquals("America/Denver", calendarsModel.getActiveCal().getZone());
  }

  @Test
  void testEditCalendarNoCalendarsExist() {
    // confirms editCalendar throws NoSuchElementException if no calendars are present.
    assertThrows(NoSuchElementException.class, () -> calendarsModel.editCalendar("Any", "name", "New"));
  }

  @Test
  void testEditCalendarNonExistentCalendar() {
    // checks editCalendar throws NoSuchElementException for a calendar that does not exist.
    calendarsModel.createCalendar("Exist", "UTC");
    assertThrows(NoSuchElementException.class, () -> calendarsModel.editCalendar("NonExist", "name", "New"));
  }

  @Test
  void testEditCalendarInvalidProperty() {
    // validates editCalendar throws IllegalArgumentException for an unknown property.
    calendarsModel.createCalendar("MyCal", "UTC");
    assertThrows(IllegalArgumentException.class, () -> calendarsModel.editCalendar("MyCal", "invalidProp", "Value"));
  }

  @Test
  void testCopySingleEventSuccessful() {
    // verifies a single event can be successfully copied between calendars.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("Meeting", "2025-07-01T09:00", "2025-07-01T10:00");

    calendarsModel.copySingleEvent("Meeting", "2025-07-01T09:00", "TargetCal", "2025-07-02T11:00");

    calendarsModel.useCalendar("TargetCal");
    Set<RobustCalEvent> targetEvents = calendarsModel.getActiveCal().getEvents();
    Optional<RobustCalEvent> copiedEvent = targetEvents.stream()
            .filter(e -> e.getSubject().equals("Meeting") &&
                    e.getStartDateTime().equals(LocalDateTime.parse("2025-07-02T11:00")))
            .findFirst();
    assertTrue(copiedEvent.isPresent());
    assertEquals(LocalDateTime.parse("2025-07-02T12:00"), copiedEvent.get().getEndDateTime()); // 1 hour duration
  }

  @Test
  void testCopySingleAllDayEventSuccessful() {
    // ensures an all-day event is copied correctly.
    calendarsModel.createCalendar("Cal1", "UTC");
    calendarsModel.createCalendar("Cal2", "UTC");
    calendarsModel.useCalendar("Cal1");
    calendarsModel.getActiveCal().createSingleAllDayEvent("Conference", "2025-07-10");

    calendarsModel.copySingleEvent("Conference", "2025-07-10T08:00", "Cal2", "2025-07-11T08:00");

    calendarsModel.useCalendar("Cal2");
    Set<RobustCalEvent> targetEvents = calendarsModel.getActiveCal().getEvents();
    Optional<RobustCalEvent> copiedEvent = targetEvents.stream()
            .filter(e -> e.getSubject().equals("Conference") &&
                    e.getStartDateTime().toLocalDate().equals(LocalDate.parse("2025-07-11")))
            .findFirst();
    assertTrue(copiedEvent.isPresent());
    assertTrue(copiedEvent.get().isAllDay());
    assertEquals(LocalDateTime.parse("2025-07-11T08:00"), copiedEvent.get().getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-07-11T17:00"), copiedEvent.get().getEndDateTime());
  }

  @Test
  void testCopySingleEventNoActiveCalendar() {
    // verifies that `copySingleEvent` throws an exception if no active calendar is set.
    calendarsModel.createCalendar("TargetCal", "UTC");
    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copySingleEvent("Event", "2025-07-01T09:00", "TargetCal", "2025-07-02T10:00"));
  }

  @Test
  void testCopySingleEventSourceEventNotFound() {
    // checks that `copySingleEvent` throws an exception if the source event does not exist.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("Existing", "2025-07-01T09:00", "2025-07-01T10:00");

    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copySingleEvent("NonExistent", "2025-07-01T09:00", "TargetCal", "2025-07-02T10:00"));
  }

  @Test
  void testCopySingleEventTargetCalendarNotFound() {
    // ensures `copySingleEvent` throws an exception if the target calendar is not found.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("Meeting", "2025-07-01T09:00", "2025-07-01T10:00");

    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copySingleEvent("Meeting", "2025-07-01T09:00", "NonExistentCal", "2025-07-02T10:00"));
  }

  @Test
  void testCopySingleEventMalformedDateTimeString() {
    // validates that `DateTimeParseException` is thrown for malformed date-time strings.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("Meeting", "2025-07-01T09:00", "2025-07-01T10:00");

    assertThrows(DateTimeParseException.class,
            () -> calendarsModel.copySingleEvent("Meeting", "invalid-date", "TargetCal", "2025-07-02T10:00"));
    assertThrows(DateTimeParseException.class,
            () -> calendarsModel.copySingleEvent("Meeting", "2025-07-01T09:00", "TargetCal", "invalid-new-date"));
  }


  @Test
  void testCopyAllEventsOnDaySuccessful() {
    // checks if all events on a specific day are correctly copied and date-shifted.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("Morning Meeting", "2025-07-15T09:00", "2025-07-15T10:00");
    calendarsModel.getActiveCal().createSingleAllDayEvent("Workshop", "2025-07-15");
    calendarsModel.getActiveCal().createSingleNormalEvent("Evening Call", "2025-07-15T17:00", "2025-07-15T18:00");

    calendarsModel.copyAllEventsOnDay("2025-07-15", "TargetCal", "2025-07-17"); // Shift by 2 days

    calendarsModel.useCalendar("TargetCal");
    Set<RobustCalEvent> targetEvents = calendarsModel.getActiveCal().getEvents();

    assertEquals(3, targetEvents.size()); // All three events should be copied

    assertTrue(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Morning Meeting") &&
            e.getStartDateTime().equals(LocalDateTime.parse("2025-07-17T09:00")) &&
            e.getEndDateTime().equals(LocalDateTime.parse("2025-07-17T10:00"))));

    assertTrue(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Workshop") &&
            e.getStartDateTime().toLocalDate().equals(LocalDate.parse("2025-07-17")) &&
            e.isAllDay()));

    assertTrue(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Evening Call") &&
            e.getStartDateTime().equals(LocalDateTime.parse("2025-07-17T17:00")) &&
            e.getEndDateTime().equals(LocalDateTime.parse("2025-07-17T18:00"))));
  }

  @Test
  void testCopyAllEventsOnDayNoEvents() {
    // verifies that copying events on a day with no events does nothing but doesn't throw.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal"); // No events created on this calendar

    assertDoesNotThrow(() -> calendarsModel.copyAllEventsOnDay("2025-08-01", "TargetCal", "2025-08-05"));

    calendarsModel.useCalendar("TargetCal");
    assertTrue(calendarsModel.getActiveCal().getEvents().isEmpty());
  }

  @Test
  void testCopyAllEventsOnDayNoActiveCalendar() {
    // checks that `copyAllEventsOnDay` throws an exception if no active calendar is set.
    calendarsModel.createCalendar("TargetCal", "UTC");
    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copyAllEventsOnDay("2025-07-01", "TargetCal", "2025-07-02"));
  }

  @Test
  void testCopyAllEventsOnDayTargetCalendarNotFound() {
    // ensures `copyAllEventsOnDay` throws an exception if the target calendar is missing.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("TestEvent", "2025-07-01T09:00", "2025-07-01T10:00");

    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copyAllEventsOnDay("2025-07-01", "NonExistentCal", "2025-07-02"));
  }

  @Test
  void testCopyAllEventsInBetweenDatesSuccessful() {
    // verifies that all events within a date range are correctly copied.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("Event A", "2025-07-01T09:00", "2025-07-01T10:00");
    calendarsModel.getActiveCal().createSingleAllDayEvent("Event B", "2025-07-05");
    calendarsModel.getActiveCal().createSingleNormalEvent("Event C", "2025-07-10T14:00", "2025-07-10T15:00");
    calendarsModel.getActiveCal().createSingleNormalEvent("Event D", "2025-07-15T10:00", "2025-07-15T11:00"); // Outside range

    calendarsModel.copyAllEventsInBetweenDates("2025-07-01", "2025-07-10", "TargetCal", "2025-07-01"); // newDateString is unused in impl

    calendarsModel.useCalendar("TargetCal");
    Set<RobustCalEvent> targetEvents = calendarsModel.getActiveCal().getEvents();

    assertEquals(3, targetEvents.size()); // A, B, C should be copied, D should not

    assertTrue(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Event A") &&
            e.getStartDateTime().equals(LocalDateTime.parse("2025-07-01T09:00"))));
    assertTrue(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Event B") &&
            e.getStartDateTime().toLocalDate().equals(LocalDate.parse("2025-07-05"))));
    assertTrue(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Event C") &&
            e.getStartDateTime().equals(LocalDateTime.parse("2025-07-10T14:00"))));
    assertFalse(targetEvents.stream().anyMatch(e -> e.getSubject().equals("Event D"))); // Not in range
  }

  @Test
  void testCopyAllEventsInBetweenDatesNoEvents() {
    // checks that copying events within a range with no events does nothing.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.createCalendar("TargetCal", "UTC");
    calendarsModel.useCalendar("SourceCal");

    assertDoesNotThrow(() -> calendarsModel.copyAllEventsInBetweenDates("2025-08-01", "2025-08-31", "TargetCal", "2025-09-01"));

    calendarsModel.useCalendar("TargetCal");
    assertTrue(calendarsModel.getActiveCal().getEvents().isEmpty());
  }

  @Test
  void testCopyAllEventsInBetweenDatesNoActiveCalendar() {
    // verifies that `copyAllEventsInBetweenDates` throws an exception if no active calendar is set.
    calendarsModel.createCalendar("TargetCal", "UTC");
    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copyAllEventsInBetweenDates("2025-07-01", "2025-07-10", "TargetCal", "2025-07-01"));
  }

  @Test
  void testCopyAllEventsInBetweenDatesTargetCalendarNotFound() {
    // ensures `copyAllEventsInBetweenDates` throws an exception if the target calendar is missing.
    calendarsModel.createCalendar("SourceCal", "UTC");
    calendarsModel.useCalendar("SourceCal");
    calendarsModel.getActiveCal().createSingleNormalEvent("TestEvent", "2025-07-01T09:00", "2025-07-01T10:00");

    assertThrows(NoSuchElementException.class,
            () -> calendarsModel.copyAllEventsInBetweenDates("2025-07-01", "2025-07-01", "NonExistentCal", "2025-07-01"));
  }
}