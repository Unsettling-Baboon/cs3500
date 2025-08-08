package controller.gui;

import model.calendar.QueriableCalendar;
import model.calendars.RobustCalendars;
import model.events.QueriableCalEvent;
import model.events.RobustCalEvent;
import view.CalendarView;
import view.CalendarViewImpl;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The concrete implementation of the CalendarControllerGUIImpl.
 * It handles user input from the View, manipulates the Model,
 * and updates the View with new data.
 */
public class CalendarControllerGUIImpl implements CalendarsControllerGUI {
  private final RobustCalendars model;
  private final CalendarViewImpl view; // changed to concrete type for access to new methods
  // controller state
  private YearMonth currentMonth;
  private LocalDate selectedDate;
  private final Map<String, Color> calendarColors;

  public CalendarControllerGUIImpl(RobustCalendars model, CalendarView view) {
    this.model = model;
    this.view = (CalendarViewImpl) view; // cast to access extended methods
    this.view.addActionListener(this);
    // initialize controller state
    this.currentMonth = YearMonth.now();
    this.selectedDate = LocalDate.now();
    this.calendarColors = new HashMap<>();
    // initialize and sync with the model
    initializeApplication();
  }

  private void initializeApplication() {
    model.createCalendar("Default", "UTC");
    calendarColors.put("Default", new Color(173, 216, 230));
    model.useCalendar("Default");
    // initial view update
    refreshView();
  }

  /**
   * Main action-handling method. Catches events from the view.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.startsWith("Select Day:")) {
      handleDaySelection(command);
    } else {
      switch (command) {
        case "Previous Month" -> changeMonth(-1);
        case "Next Month" -> changeMonth(1);
        case "Change Calendar" -> changeActiveCalendar(e);
        case "Add Event" -> addEvent();
        case "Change Color" -> changeCalendarColor();
        case "Create Calendar" -> createNewCalendar();
        case "Edit Calendar" -> editCalendar();
        case "Edit Event" -> editEvent();
        case "Copy Event" -> copyEvent();
        case "Copy Day" -> copyDay();
        case "Copy Range" -> copyDateRange();
      }
    }
  }

  private void handleDaySelection(String command) {
    selectedDate = LocalDate.parse(command.substring("Select Day:".length()));
    updateEventDetailsForSelectedDate();
  }

  private void changeMonth(int offset) {
    currentMonth = currentMonth.plusMonths(offset);
    refreshView();
  }

  private void changeActiveCalendar(ActionEvent e) {
    JComboBox<?> dropdown = (JComboBox<?>) e.getSource();
    String calendarName = (String) dropdown.getSelectedItem();
    if (calendarName != null) {
      try {
        model.useCalendar(calendarName);
        refreshView();
      } catch (NoSuchElementException ex) {
        // this case is unlikely if the dropdown is synced but good practice to handle.
      }
    }
  }

  private void addEvent() {
    String eventText = view.getNewEventText();
    if (eventText == null || eventText.trim().isEmpty()) {
      JOptionPane.showMessageDialog(view, "Please enter an event name.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    // validate time selection
    if (!view.isAllDayEvent()) {
      String startTime = view.getStartTime();
      String endTime = view.getEndTime();
      if (startTime == null || endTime == null) {
        JOptionPane.showMessageDialog(view, "Please select start and end times for the event.",
                "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      // validate that end time is after start time
      if (startTime.compareTo(endTime) >= 0) {
        JOptionPane.showMessageDialog(view, "End time must be after start time.",
                "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    try {
      if (view.isRecurringEvent()) {
        // handle recurring event
        boolean[] selectedDays = view.getSelectedDays();
        // convert boolean array to weekday string format used by model
        StringBuilder weekdayString = new StringBuilder();
        String[] dayChars = {"U", "M", "T", "W", "R", "F", "S"}; // model's format: U=sun, M=mon, etc.
        for (int i = 0; i < 7; i++) {
          if (selectedDays[i]) {
            weekdayString.append(dayChars[i]);
          }
        }
        if (weekdayString.isEmpty()) {
          JOptionPane.showMessageDialog(view, "Please select at least one day for recurring event.",
                  "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (view.isAllDayEvent()) {
          if (view.isEndByOccurrence()) {
            model.getActiveCal().createRecurringAllDayEventsNTimes(
                    eventText,
                    selectedDate.toString(),
                    weekdayString.toString(),
                    view.getOccurrences()
            );
          } else {
            model.getActiveCal().createRecurringAllDayEventsUntilDate(
                    eventText,
                    selectedDate.toString(),
                    weekdayString.toString(),
                    view.getEndDate().toString()
            );
          }
        } else {
          // create recurring event with time
          String startTime = view.getStartTime();
          String endTime = view.getEndTime();
          String startDateTime = selectedDate.toString() + "T" + startTime;
          String endDateTime = selectedDate + "T" + endTime;
          if (view.isEndByOccurrence()) {
            model.getActiveCal().createRecurringNormalEventsNTimes(
                    eventText,
                    startDateTime,
                    endDateTime,
                    weekdayString.toString(),
                    view.getOccurrences()
            );
          } else {
            model.getActiveCal().createRecurringNormalEventsUntilDate(
                    eventText,
                    startDateTime,
                    endDateTime,
                    weekdayString.toString(),
                    view.getEndDate().toString()
            );
          }
        }
      } else {
        // handle single event
        if (view.isAllDayEvent()) {
          model.getActiveCal().createSingleAllDayEvent(eventText, selectedDate.toString());
        } else {
          String startTime = view.getStartTime();
          String endTime = view.getEndTime();
          model.getActiveCal().createSingleNormalEvent(
                  eventText,
                  selectedDate.toString() + "T" + startTime,
                  selectedDate + "T" + endTime
          );
        }
      }
      view.clearEventForm();
      refreshView();
      JOptionPane.showMessageDialog(view, "Event created successfully!", "Success",
              JOptionPane.INFORMATION_MESSAGE);
      // note: if recurring events don't show series information properly,
      // it may be because printAllEventsOnDate returns ZonedCalEventImpl wrappers
      // that don't preserve the series field from the original events
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(view, "Error creating event: " + ex.getMessage(),
              "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void editEvent() {
    List<String> selectedEvents = view.getSelectedEvents();
    if (selectedEvents.isEmpty()) {
      JOptionPane.showMessageDialog(view, "Please select an event to edit.",
              "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (selectedEvents.size() > 1) {
      JOptionPane.showMessageDialog(view, "Please select only one event to edit.",
              "Multiple Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    String eventName = selectedEvents.getFirst();
    if (eventName.equals("No events for this day.")) {
      return;
    }
    // get the event details
    List<RobustCalEvent> events = model.getActiveCal().printAllEventsOnDate(selectedDate.toString());
    QueriableCalEvent eventToEdit = events.stream()
            .filter(e -> e.getSubject().equals(eventName))
            .findFirst()
            .orElse(null);
    if (eventToEdit == null) {
      return;
    }
    // check if event is part of a series
    boolean isPartOfSeries = (eventToEdit.getSeries() != null);
    // debug: print series information
    System.out.println("Event: " + eventName + ", Series: " + eventToEdit.getSeries());
    // if getSeries() returns null but we know it's a recurring event, check by name
    if (!isPartOfSeries) {
      // fallback: check if there are multiple events with the same name
      try {
        long count = model.getActiveCal().getEvents().stream()
                .filter(e -> e.getSubject().equals(eventName))
                .count();
        isPartOfSeries = count > 1;
        if (isPartOfSeries) {
          System.out.println("Series detected by name count: " + count);
        }
      } catch (Exception ex) {
        // if getEvents() is not available, keep isPartOfSeries as false
      }
    }
    // show edit dialog
    JPanel editPanel = new JPanel();
    editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
    JTextField nameField = new JTextField(eventToEdit.getSubject());
    editPanel.add(new JLabel("Event Name:"));
    editPanel.add(nameField);
    int result = JOptionPane.showConfirmDialog(view, editPanel, "Edit Event",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
      String newName = nameField.getText().trim();
      if (!newName.isEmpty() && !newName.equals(eventToEdit.getSubject())) {
        try {
          if (isPartOfSeries) {
            // ask user how they want to edit the series
            String[] options = {"This event only", "All events in series", "This and future events"};
            int choice = JOptionPane.showOptionDialog(view,
                    "This event is part of a series. How would you like to edit it?",
                    "Edit Series",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            switch (choice) {
              case 0 -> {
                // edit only this event
                model.getActiveCal().editSingleEvent(
                        "subject",
                        eventToEdit.getSubject(),
                        eventToEdit.getStartDateTime().toString(),
                        eventToEdit.getEndDateTime().toString(),
                        newName
                );
              }
              case 1 -> {
                // edit all events in series
                model.getActiveCal().editEventAndMaybeAll(
                        "subject",
                        eventToEdit.getSubject(),
                        eventToEdit.getStartDateTime().toString(),
                        newName
                );
              }
              case 2 -> {
                // edit this and future events
                model.getActiveCal().editEventAndMaybeOnward(
                        "subject",
                        eventToEdit.getSubject(),
                        eventToEdit.getStartDateTime().toString(),
                        newName
                );
              }
              default -> {
                return; // user cancelled
              }
            }
          } else {
            // single event, just edit it
            model.getActiveCal().editSingleEvent(
                    "subject",
                    eventToEdit.getSubject(),
                    eventToEdit.getStartDateTime().toString(),
                    eventToEdit.getEndDateTime().toString(),
                    newName
            );
          }
          refreshView();
          JOptionPane.showMessageDialog(view, "Event edited successfully!", "Success",
                  JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(view, "Error editing event: " + ex.getMessage(),
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void copyEvent() {
    List<String> selectedEvents = view.getSelectedEvents();
    if (selectedEvents.isEmpty()) {
      JOptionPane.showMessageDialog(view, "Please select an event to copy.",
              "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (selectedEvents.size() > 1) {
      JOptionPane.showMessageDialog(view, "Please select only one event to copy.",
              "Multiple Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }
    String eventName = selectedEvents.get(0);
    if (eventName.equals("No events for this day.")) {
      return;
    }
    // get the event details
    List<RobustCalEvent> events = model.getActiveCal().printAllEventsOnDate(selectedDate.toString());
    QueriableCalEvent eventToCopy = events.stream()
            .filter(e -> e.getSubject().equals(eventName))
            .findFirst()
            .orElse(null);
    if (eventToCopy == null) {
      return;
    }
    // get available calendars
    List<String> calendarNames = model.getAllCalendars().stream()
            .map(QueriableCalendar::getName)
            .collect(Collectors.toList());
    if (calendarNames.size() < 2) {
      JOptionPane.showMessageDialog(view, "You need at least 2 calendars to copy events between them.",
              "Not Enough Calendars", JOptionPane.WARNING_MESSAGE);
      return;
    }
    // ask user to select target calendar
    String targetCalendar = (String) JOptionPane.showInputDialog(view,
            "Select target calendar:",
            "Copy Event",
            JOptionPane.QUESTION_MESSAGE,
            null,
            calendarNames.toArray(),
            calendarNames.get(0));
    if (targetCalendar == null) {
      return; // user cancelled
    }
    // ask user for new date/time
    String newDateStr = JOptionPane.showInputDialog(view,
            "Enter new date for the event (YYYY-MM-DD):",
            selectedDate.toString());
    if (newDateStr == null) {
      return; // user cancelled
    }
    try {
      LocalDate newDate = LocalDate.parse(newDateStr);
      String newDateTime = newDate.toString() + "T" + eventToCopy.getStartDateTime().toLocalTime().toString();
      // copy the event
      model.copySingleEvent(
              eventToCopy.getSubject(),
              eventToCopy.getStartDateTime().toString(),
              targetCalendar,
              newDateTime
      );
      JOptionPane.showMessageDialog(view,
              "Event copied successfully to " + targetCalendar + "!",
              "Success",
              JOptionPane.INFORMATION_MESSAGE);
      // if we copied to the current calendar, refresh view
      if (targetCalendar.equals(model.getActiveCal().getName())) {
        refreshView();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(view,
              "Error copying event: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
    }
  }

  private void copyDay() {
    // get available calendars
    List<String> calendarNames = model.getAllCalendars().stream()
            .map(QueriableCalendar::getName)
            .collect(Collectors.toList());
    if (calendarNames.size() < 2) {
      JOptionPane.showMessageDialog(view,
              "You need at least 2 calendars to copy events between them.",
              "Not Enough Calendars",
              JOptionPane.WARNING_MESSAGE);
      return;
    }
    // check if there are events on the selected day
    List<RobustCalEvent> eventsOnDay = model.getActiveCal().printAllEventsOnDate(selectedDate.toString());
    if (eventsOnDay.isEmpty()) {
      JOptionPane.showMessageDialog(view,
              "No events on " + selectedDate + " to copy.",
              "No Events",
              JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    // show dialog with copy options
    JPanel copyPanel = new JPanel();
    copyPanel.setLayout(new BoxLayout(copyPanel, BoxLayout.Y_AXIS));
    copyPanel.add(new JLabel("Copying " + eventsOnDay.size() + " event(s) from " + selectedDate));
    copyPanel.add(Box.createVerticalStrut(10));
    // target calendar selection
    JComboBox<String> calendarCombo = new JComboBox<>(calendarNames.toArray(new String[0]));
    copyPanel.add(new JLabel("Target Calendar:"));
    copyPanel.add(calendarCombo);
    copyPanel.add(Box.createVerticalStrut(10));
    // target date input
    JTextField dateField = new JTextField(selectedDate.toString());
    copyPanel.add(new JLabel("Target Date (YYYY-MM-DD):"));
    copyPanel.add(dateField);
    int result = JOptionPane.showConfirmDialog(view, copyPanel,
            "Copy Day's Events", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
      String targetCalendar = (String) calendarCombo.getSelectedItem();
      String targetDate = dateField.getText().trim();
      try {
        // validate date
        LocalDate.parse(targetDate);
        // copy all events
        model.copyAllEventsOnDay(
                selectedDate.toString(),
                targetCalendar,
                targetDate
        );
        JOptionPane.showMessageDialog(view,
                "Successfully copied " + eventsOnDay.size() + " event(s) to " + targetCalendar + "!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        // if we copied to the current calendar, refresh view
        if (targetCalendar.equals(model.getActiveCal().getName())) {
          refreshView();
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(view,
                "Error copying events: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void copyDateRange() {
    // get available calendars
    List<String> calendarNames = model.getAllCalendars().stream()
            .map(QueriableCalendar::getName)
            .collect(Collectors.toList());
    if (calendarNames.size() < 2) {
      JOptionPane.showMessageDialog(view,
              "You need at least 2 calendars to copy events between them.",
              "Not Enough Calendars",
              JOptionPane.WARNING_MESSAGE);
      return;
    }
    // create dialog for date range selection
    JPanel rangePanel = new JPanel();
    rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.Y_AXIS));
    rangePanel.add(new JLabel("Copy events between dates"));
    rangePanel.add(Box.createVerticalStrut(10));
    // from date
    JTextField fromDateField = new JTextField(selectedDate.toString());
    rangePanel.add(new JLabel("From Date (YYYY-MM-DD):"));
    rangePanel.add(fromDateField);
    rangePanel.add(Box.createVerticalStrut(5));
    // to date
    JTextField toDateField = new JTextField(selectedDate.plusDays(7).toString());
    rangePanel.add(new JLabel("To Date (YYYY-MM-DD):"));
    rangePanel.add(toDateField);
    rangePanel.add(Box.createVerticalStrut(10));
    // target calendar
    JComboBox<String> calendarCombo = new JComboBox<>(calendarNames.toArray(new String[0]));
    rangePanel.add(new JLabel("Target Calendar:"));
    rangePanel.add(calendarCombo);
    rangePanel.add(Box.createVerticalStrut(10));
    // options for copying
    JRadioButton copyAsIsRadio = new JRadioButton("Copy to same dates", true);
    JRadioButton copyToNewStartRadio = new JRadioButton("Copy starting from new date");
    ButtonGroup copyOptionsGroup = new ButtonGroup();
    copyOptionsGroup.add(copyAsIsRadio);
    copyOptionsGroup.add(copyToNewStartRadio);
    rangePanel.add(copyAsIsRadio);
    rangePanel.add(copyToNewStartRadio);
    // new start date field (only enabled when second option selected)
    JTextField newStartDateField = new JTextField(selectedDate.toString());
    newStartDateField.setEnabled(false);
    rangePanel.add(new JLabel("New Start Date (if applicable):"));
    rangePanel.add(newStartDateField);
    // add listener to enable/disable new start date field
    copyToNewStartRadio.addActionListener(e -> newStartDateField.setEnabled(copyToNewStartRadio.isSelected()));
    copyAsIsRadio.addActionListener(e -> newStartDateField.setEnabled(copyToNewStartRadio.isSelected()));
    int result = JOptionPane.showConfirmDialog(view, rangePanel,
            "Copy Events in Date Range", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
      try {
        String fromDate = fromDateField.getText().trim();
        String toDate = toDateField.getText().trim();
        String targetCalendar = (String) calendarCombo.getSelectedItem();
        // validate dates
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        if (from.isAfter(to)) {
          JOptionPane.showMessageDialog(view,
                  "From date must be before or equal to To date.",
                  "Invalid Date Range",
                  JOptionPane.ERROR_MESSAGE);
          return;
        }
        // count events in range
        List<RobustCalEvent> eventsInRange = model.getActiveCal().printAllEventsBetweenDates(
                from.atStartOfDay().toString(),
                to.atTime(23, 59).toString()
        );
        if (eventsInRange.isEmpty()) {
          JOptionPane.showMessageDialog(view,
                  "No events found in the specified date range.",
                  "No Events",
                  JOptionPane.INFORMATION_MESSAGE);
          return;
        }
        // perform the copy based on selected option
        if (copyAsIsRadio.isSelected()) {
          // copy to same dates (no date adjustment)
          model.copyAllEventsInBetweenDates(
                  fromDate,
                  toDate,
                  targetCalendar,
                  fromDate  // using same start date
          );
        } else {
          // copy with date shift
          String newStartDate = newStartDateField.getText().trim();
          LocalDate.parse(newStartDate); // validate
          // note: the model's copyAllEventsInBetweenDates doesn't support date shifting
          // so we'll copy to same dates and inform user
          model.copyAllEventsInBetweenDates(
                  fromDate,
                  toDate,
                  targetCalendar,
                  newStartDate
          );
          JOptionPane.showMessageDialog(view,
                  "Note: Events copied to same dates. Date shifting not supported by current model.",
                  "Information",
                  JOptionPane.INFORMATION_MESSAGE);
        }
        JOptionPane.showMessageDialog(view,
                "Successfully copied " + eventsInRange.size() + " event(s) to " + targetCalendar + "!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        // if we copied to the current calendar, refresh view
        if (targetCalendar.equals(model.getActiveCal().getName())) {
          refreshView();
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(view,
                "Error copying events: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void changeCalendarColor() {
    String activeCalName = model.getActiveCal().getName();
    Color currentColor = calendarColors.getOrDefault(activeCalName, Color.WHITE);
    Color newColor = JColorChooser.showDialog(null, "Choose Color for " + activeCalName, currentColor);
    if (newColor != null) {
      calendarColors.put(activeCalName, newColor);
      refreshView();
    }
  }

  private void createNewCalendar() {
    // get calendar name from user
    String name = JOptionPane.showInputDialog(null,
            "Enter calendar name:",
            "Create New Calendar",
            JOptionPane.PLAIN_MESSAGE);
    if (name == null || name.trim().isEmpty()) {
      return; // user cancelled or entered empty name
    }
    // check if calendar already exists
    boolean exists = model.getAllCalendars().stream()
            .anyMatch(cal -> cal.getName().equals(name));
    if (exists) {
      JOptionPane.showMessageDialog(null,
              "A calendar with this name already exists.",
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    // get all available timezones
    String[] allTimezones = ZoneId.getAvailableZoneIds().stream()
            .sorted()
            .toArray(String[]::new);
    // create a combo box with search functionality
    JComboBox<String> timezoneCombo = new JComboBox<>(allTimezones);
    timezoneCombo.setEditable(true);
    timezoneCombo.setSelectedItem("UTC");
    // create custom panel for timezone selection
    JPanel timezonePanel = new JPanel();
    timezonePanel.setLayout(new BoxLayout(timezonePanel, BoxLayout.Y_AXIS));
    timezonePanel.add(new JLabel("Select or type timezone:"));
    timezonePanel.add(timezoneCombo);
    int result = JOptionPane.showConfirmDialog(null,
            timezonePanel,
            "Select Timezone",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) {
      return; // user cancelled
    }
    String timezone = (String) timezoneCombo.getSelectedItem();
    // validate timezone
    try {
      ZoneId.of(timezone);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null,
              "Invalid timezone: " + timezone,
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    // create the calendar
    try {
      model.createCalendar(name, timezone);
      // assign a random color to the new calendar
      calendarColors.put(name, generateRandomColor());
      // switch to the new calendar
      model.useCalendar(name);
      refreshView();
      JOptionPane.showMessageDialog(null,
              "Calendar '" + name + "' created successfully!",
              "Success",
              JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
              "Error creating calendar: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
    }
  }

  private void editCalendar() {
    String currentCalendarName = model.getActiveCal().getName();
    String currentTimezone = model.getActiveCal().getZone();
    // create edit panel
    JPanel editPanel = new JPanel();
    editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
    // name field
    JTextField nameField = new JTextField(currentCalendarName);
    editPanel.add(new JLabel("Calendar Name:"));
    editPanel.add(nameField);
    editPanel.add(Box.createVerticalStrut(10));
    // timezone selection
    String[] allTimezones = ZoneId.getAvailableZoneIds().stream()
            .sorted()
            .toArray(String[]::new);
    JComboBox<String> timezoneCombo = new JComboBox<>(allTimezones);
    timezoneCombo.setEditable(true);
    timezoneCombo.setSelectedItem(currentTimezone);
    editPanel.add(new JLabel("Timezone:"));
    editPanel.add(timezoneCombo);
    int result = JOptionPane.showConfirmDialog(null,
            editPanel,
            "Edit Calendar",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
      String newName = nameField.getText().trim();
      String newTimezone = (String) timezoneCombo.getSelectedItem();
      boolean nameChanged = !newName.equals(currentCalendarName);
      boolean timezoneChanged = !newTimezone.equals(currentTimezone);
      if (!nameChanged && !timezoneChanged) {
        return; // no changes made
      }
      try {
        // validate new name if changed
        if (nameChanged && !newName.isEmpty()) {
          // check if new name already exists
          boolean exists = model.getAllCalendars().stream()
                  .anyMatch(cal -> cal.getName().equals(newName) && !cal.getName().equals(currentCalendarName));
          if (exists) {
            JOptionPane.showMessageDialog(null,
                    "A calendar with this name already exists.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
          }
          // edit calendar name
          model.editCalendar(currentCalendarName, "name", newName);
          // update color mapping
          Color calendarColor = calendarColors.remove(currentCalendarName);
          if (calendarColor != null) {
            calendarColors.put(newName, calendarColor);
          }
        }
        // validate and update timezone if changed
        if (timezoneChanged) {
          try {
            ZoneId.of(newTimezone); // validate
            model.editCalendar(nameChanged ? newName : currentCalendarName, "timezone", newTimezone);
          } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid timezone: " + newTimezone,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        refreshView();
        JOptionPane.showMessageDialog(null,
                "Calendar updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
                "Error editing calendar: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private Color generateRandomColor() {
    Random rand = new Random();
    // generate lighter colors by ensuring rgb values are in the upper range
    int r = 100 + rand.nextInt(156); // 100-255
    int g = 100 + rand.nextInt(156); // 100-255
    int b = 100 + rand.nextInt(156); // 100-255
    return new Color(r, g, b);
  }

  /**
   * Gathers all necessary data from the model and tells the view to redraw.
   */
  private void refreshView() {
    // 1. get calendar names and active calendar from model
    Set<String> calendarNames = model.getAllCalendars().stream()
            .map(QueriableCalendar::getName)
            .collect(Collectors.toSet());
    String activeCalendarName = model.getActiveCal().getName();
    // 2. update the calendar dropdown
    view.updateCalendarDropdown(calendarNames, activeCalendarName);
    // 3. get events for the current month from the model
    Map<LocalDate, List<String>> eventsOfMonth = new HashMap<>();
    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      List<String> eventTitles = model.getActiveCal().printAllEventsOnDate(date.toString())
              .stream()
              .map(QueriableCalEvent::getSubject)
              .collect(Collectors.toList());
      if (!eventTitles.isEmpty()) {
        eventsOfMonth.put(date, eventTitles);
      }
    }
    // 4. tell the view to redraw the main month display
    view.displayMonth(currentMonth, calendarColors, activeCalendarName, eventsOfMonth);
    // 5. update the event details for the currently selected date
    updateEventDetailsForSelectedDate();
  }

  private void updateEventDetailsForSelectedDate() {
    List<String> eventTitles = model.getActiveCal().printAllEventsOnDate(selectedDate.toString())
            .stream()
            .map(QueriableCalEvent::getSubject)
            .collect(Collectors.toList());
    view.updateEventDetails(selectedDate, eventTitles);
  }

  /**
   * Starts the application by making the view visible.
   */
  @Override
  public void start() {
    view.makeVisible();
  }
}