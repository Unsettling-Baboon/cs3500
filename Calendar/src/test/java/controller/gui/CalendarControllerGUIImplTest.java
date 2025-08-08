package controller.gui;

import model.calendars.MockCalendars;
import org.junit.Test;
import view.CalendarView;
import view.CalendarViewImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CalendarControllerGUIImpl using MockCalendars.
 */
public class CalendarControllerGUIImplTest {
  private MockCalendars mockModel;
  private TestCalendarView mockView;
  private CalendarControllerGUIImpl controller;

  /**
   * Mock implementation of CalendarView for testing.
   */
  private static class TestCalendarView implements CalendarView {
    private final List<String> actionLog = new ArrayList<>();
    private String newEventText = "";
    private boolean allDayEvent = false;
    private boolean recurringEvent = false;
    private String startTime = "09:00";
    private String endTime = "10:00";
    private boolean[] selectedDays = new boolean[7];
    private boolean endByOccurrence = true;
    private int occurrences = 10;
    private LocalDate endDate = LocalDate.now().plusMonths(1);
    private List<String> selectedEvents = new ArrayList<>();
    private Set<String> calendarNames = new HashSet<>();
    private String selectedCalendar = "";

    @Override
    public void addActionListener(java.awt.event.ActionListener listener) {
      actionLog.add("addActionListener called");
    }

    @Override
    public void displayMonth(YearMonth monthToDisplay, Map<String, Color> calendarColors,
                             String activeCalendar, Map<LocalDate, List<String>> eventsOfMonth) {
      actionLog.add(String.format("displayMonth: %s, activeCalendar: %s", monthToDisplay, activeCalendar));
    }

    @Override
    public void updateEventDetails(LocalDate date, List<String> eventTitles) {
      actionLog.add(String.format("updateEventDetails: %s, events: %s", date, eventTitles));
    }

    @Override
    public void updateCalendarDropdown(Set<String> calendarNames, String selected) {
      this.calendarNames = calendarNames;
      this.selectedCalendar = selected;
      actionLog.add(String.format("updateCalendarDropdown: calendars=%s, selected=%s", calendarNames, selected));
    }

    @Override
    public String getNewEventText() {
      return newEventText;
    }

    @Override
    public void makeVisible() {
      actionLog.add("makeVisible called");
    }

    @Override
    public void clearEventForm() {
      actionLog.add("clearEventForm called");
      newEventText = "";
      allDayEvent = false;
      recurringEvent = false;
      endByOccurrence = true;
      occurrences = 10;
    }

    @Override
    public boolean isAllDayEvent() {
      return allDayEvent;
    }

    @Override
    public boolean isRecurringEvent() {
      return recurringEvent;
    }

    @Override
    public String getStartTime() {
      return startTime;
    }

    @Override
    public String getEndTime() {
      return endTime;
    }

    @Override
    public boolean[] getSelectedDays() {
      return selectedDays;
    }

    @Override
    public boolean isEndByOccurrence() {
      return endByOccurrence;
    }

    @Override
    public int getOccurrences() {
      return occurrences;
    }

    @Override
    public LocalDate getEndDate() {
      return endDate;
    }

    @Override
    public List<String> getSelectedEvents() {
      return selectedEvents;
    }

    // test helper methods
    public List<String> getActionLog() {
      return actionLog;
    }

    public void setNewEventText(String text) {
      this.newEventText = text;
    }

    public void setAllDayEvent(boolean allDay) {
      this.allDayEvent = allDay;
    }

    public void setRecurringEvent(boolean recurring) {
      this.recurringEvent = recurring;
    }

    public void setSelectedDays(boolean[] days) {
      this.selectedDays = days;
    }

    public void setSelectedEvents(List<String> events) {
      this.selectedEvents = events;
    }

    public void setStartTime(String time) {
      this.startTime = time;
    }

    public void setEndTime(String time) {
      this.endTime = time;
    }

    public void setEndByOccurrence(boolean byOccurrence) {
      this.endByOccurrence = byOccurrence;
    }

    public void setOccurrences(int num) {
      this.occurrences = num;
    }

    public void setEndDate(LocalDate date) {
      this.endDate = date;
    }
  }

  @Before
  public void setUp() {
    mockModel = new MockCalendars();
    mockView = new TestCalendarView();
    controller = new CalendarControllerGUIImpl(mockModel, mockView);
  }

  @Test
  public void testInitialization() {
    String log = mockModel.getLog();
    assertTrue("Should create default calendar", log.contains("createCalendar(name='Default', timeZone='UTC')"));
    assertTrue("Should use default calendar", log.contains("useCalendar(name='Default')"));
    assertTrue("View should be updated", mockView.getActionLog().contains("updateCalendarDropdown: calendars=[], selected="));
  }

  @Test
  public void testStartMethod() {
    controller.start();
    assertTrue("View should be made visible", mockView.getActionLog().contains("makeVisible called"));
  }

  @Test
  public void testDaySelection() {
    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(event);
    assertTrue("Should update event details for selected date",
            mockView.getActionLog().stream().anyMatch(s -> s.contains("updateEventDetails: 2025-06-20")));
  }

  @Test
  public void testMonthNavigation() {
    // test next month
    ActionEvent nextEvent = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Next Month");
    controller.actionPerformed(nextEvent);
    assertTrue("Should display next month",
            mockView.getActionLog().stream().anyMatch(s -> s.contains("displayMonth") && s.contains("JULY")));

    // test previous month
    ActionEvent prevEvent = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Previous Month");
    controller.actionPerformed(prevEvent);
    controller.actionPerformed(prevEvent);
    assertTrue("Should display previous month",
            mockView.getActionLog().stream().anyMatch(s -> s.contains("displayMonth")));
  }

  @Test
  public void testAddSingleAllDayEvent() {
    mockView.setNewEventText("Test Event");
    mockView.setAllDayEvent(true);
    mockView.setRecurringEvent(false);

    // select a day first
    ActionEvent selectDay = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(selectDay);

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Add Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should attempt to get active calendar", log.contains("getActiveCal() called"));
    assertTrue("View should clear form", mockView.getActionLog().contains("clearEventForm called"));
  }

  @Test
  public void testAddSingleTimedEvent() {
    mockView.setNewEventText("Meeting");
    mockView.setAllDayEvent(false);
    mockView.setRecurringEvent(false);
    mockView.setStartTime("14:00");
    mockView.setEndTime("15:30");

    // select a day first
    ActionEvent selectDay = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(selectDay);

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Add Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should attempt to get active calendar", log.contains("getActiveCal() called"));
  }

  @Test
  public void testAddRecurringEvent() {
    mockView.setNewEventText("Weekly Meeting");
    mockView.setAllDayEvent(false);
    mockView.setRecurringEvent(true);
    mockView.setStartTime("10:00");
    mockView.setEndTime("11:00");
    mockView.setSelectedDays(new boolean[]{false, true, false, true, false, false, false}); // mon and wed
    mockView.setEndByOccurrence(true);
    mockView.setOccurrences(5);

    // select a day first
    ActionEvent selectDay = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(selectDay);

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Add Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should attempt to get active calendar", log.contains("getActiveCal() called"));
  }

  @Test
  public void testAddEventValidation_EmptyName() {
    mockView.setNewEventText("");

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Add Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertFalse("Should not create event with empty name", log.contains("createSingleAllDayEvent"));
  }

  @Test
  public void testAddEventValidation_InvalidTimes() {
    mockView.setNewEventText("Invalid Event");
    mockView.setAllDayEvent(false);
    mockView.setStartTime("15:00");
    mockView.setEndTime("14:00"); // end before start

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Add Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertFalse("Should not create event with invalid times", log.contains("createSingleNormalEvent"));
  }

  @Test
  public void testAddRecurringEventValidation_NoDaysSelected() {
    mockView.setNewEventText("No Days Event");
    mockView.setRecurringEvent(true);
    mockView.setSelectedDays(new boolean[7]); // all false

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Add Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertFalse("Should not create recurring event with no days selected", log.contains("createRecurring"));
  }

  @Test
  public void testCalendarSwitching() {
    JComboBox<String> combo = new JComboBox<>();
    combo.setSelectedItem("TestCalendar");
    ActionEvent event = new ActionEvent(combo, ActionEvent.ACTION_PERFORMED, "Change Calendar");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should use selected calendar", log.contains("useCalendar(name='TestCalendar')"));
  }

  @Test
  public void testCreateCalendar() {
    // note: this test won't show dialogs, but we can test the action is registered
    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Create Calendar");
    // the actual creation would require mocking JOptionPane which is complex
    // here we just verify the action is handled without exception
    try {
      controller.actionPerformed(event);
      assertTrue("Action should be handled", true);
    } catch (Exception e) {
      fail("Create calendar action should not throw exception");
    }
  }

  @Test
  public void testEditEvent() {
    mockView.setSelectedEvents(Arrays.asList("Test Event"));

    // select a day first
    ActionEvent selectDay = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(selectDay);

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Edit Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should attempt to get active calendar", log.contains("getActiveCal() called"));
  }

  @Test
  public void testEditEventValidation_NoSelection() {
    mockView.setSelectedEvents(new ArrayList<>());

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Edit Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertFalse("Should not edit without selection", log.contains("editSingleEvent"));
  }

  @Test
  public void testEditEventValidation_MultipleSelection() {
    mockView.setSelectedEvents(Arrays.asList("Event 1", "Event 2"));

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Edit Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertFalse("Should not edit with multiple selection", log.contains("editSingleEvent"));
  }

  @Test
  public void testCopyEvent() {
    mockView.setSelectedEvents(Arrays.asList("Event to Copy"));

    // select a day first
    ActionEvent selectDay = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(selectDay);

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Copy Event");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should attempt to get calendars", log.contains("getActiveCal() called"));
  }

  @Test
  public void testCopyDay() {
    // select a day first
    ActionEvent selectDay = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Select Day:2025-06-20");
    controller.actionPerformed(selectDay);

    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Copy Day");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should get all calendars", log.contains("getActiveCal() called"));
  }

  @Test
  public void testCopyDateRange() {
    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Copy Range");
    controller.actionPerformed(event);

    // this would normally show dialogs, but we can verify it doesn't crash
    assertTrue("Action should be handled", true);
  }

  @Test
  public void testChangeColor() {
    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Change Color");
    // this would normally show a color chooser dialog
    try {
      controller.actionPerformed(event);
      assertTrue("Action should be handled", true);
    } catch (Exception e) {
      fail("Change color action should not throw exception");
    }
  }

  @Test
  public void testEditCalendar() {
    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Edit Calendar");
    controller.actionPerformed(event);

    String log = mockModel.getLog();
    assertTrue("Should get active calendar", log.contains("getActiveCal() called"));
  }

  @Test
  public void testUnknownAction() {
    ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "Unknown Action");
    // should handle gracefully without exception
    try {
      controller.actionPerformed(event);
      assertTrue("Unknown action should be ignored", true);
    } catch (Exception e) {
      fail("Unknown action should not throw exception");
    }
  }
}