package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Swing-based implementation of the CalendarView interface.
 * This class builds the GUI but contains no application logic.
 */
public class CalendarViewImpl extends JFrame implements CalendarView {
  // main frame and panels
  private final JPanel calendarPanel;
  private final JLabel monthLabel;
  private final JComboBox<String> calendarDropdown;
  // features panel components
  private JLabel selectedDayLabel;
  private DefaultListModel<String> eventListModel;
  private JList<String> eventList;
  private JTextField newEventField;
  private JButton addEventButton;
  private JButton changeColorButton;
  private JButton createCalendarButton;
  private JButton editEventButton;
  private JButton editCalendarButton;
  private JButton copyEventButton;
  private JButton copyDayButton;
  private JButton copyRangeButton;
  private final JButton prevButton;
  private final JButton nextButton;
  // event creation components
  private JRadioButton singleEventRadio;
  private JRadioButton recurringEventRadio;
  private JPanel recurringOptionsPanel;
  private JCheckBox[] dayCheckboxes;
  private JRadioButton byOccurrenceRadio;
  private JRadioButton byEndDateRadio;
  private JSpinner occurrenceSpinner;
  private JSpinner endDateSpinner;
  private JComboBox<String> startTimeCombo;
  private JComboBox<String> endTimeCombo;
  private JCheckBox allDayCheckbox;

  public CalendarViewImpl() {
    super("Calendar App (MVC)");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(1500, 700);
    this.setLayout(new BorderLayout());
    // top panel for calendar navigation
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    this.prevButton = new JButton("<");
    this.nextButton = new JButton(">");
    this.monthLabel = new JLabel();
    this.calendarDropdown = new JComboBox<>();
    topPanel.add(prevButton);
    topPanel.add(monthLabel);
    topPanel.add(nextButton);
    topPanel.add(new JLabel("  Calendar:"));
    topPanel.add(calendarDropdown);
    this.add(topPanel, BorderLayout.NORTH);
    // main calendar panel
    this.calendarPanel = new JPanel();
    this.add(calendarPanel, BorderLayout.CENTER);
    // features panel (side panel)
    JPanel featuresPanel = createFeaturesPanel();
    JScrollPane featuresScroll = new JScrollPane(featuresPanel);
    featuresScroll.setPreferredSize(new Dimension(800, 0));
    this.add(featuresScroll, BorderLayout.EAST);
    // set action commands for the controller
    setActionCommands();
  }

  private JPanel createFeaturesPanel() {
    JPanel featuresPanel = new JPanel();
    featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.PAGE_AXIS));
    featuresPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    // selected day and events list
    this.selectedDayLabel = new JLabel("No date selected");
    TitledBorder eventsBorder = BorderFactory.createTitledBorder("Events");
    this.selectedDayLabel.setBorder(eventsBorder);
    featuresPanel.add(selectedDayLabel);
    this.eventListModel = new DefaultListModel<>();
    this.eventList = new JList<>(eventListModel);
    eventList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    JScrollPane scrollPane = new JScrollPane(eventList);
    scrollPane.setPreferredSize(new Dimension(300, 150));
    featuresPanel.add(scrollPane);
    // event action buttons
    JPanel eventActionsPanel = new JPanel(new FlowLayout());
    this.editEventButton = new JButton("Edit Event");
    this.copyEventButton = new JButton("Copy Event");
    eventActionsPanel.add(editEventButton);
    eventActionsPanel.add(copyEventButton);
    featuresPanel.add(eventActionsPanel);
    // add new event area
    JPanel addEventPanel = new JPanel();
    addEventPanel.setLayout(new BoxLayout(addEventPanel, BoxLayout.Y_AXIS));
    addEventPanel.setBorder(BorderFactory.createTitledBorder("Add New Event"));
    // event name field
    JPanel namePanel = new JPanel(new BorderLayout());
    namePanel.add(new JLabel("Event Name:"), BorderLayout.WEST);
    this.newEventField = new JTextField();
    namePanel.add(newEventField, BorderLayout.CENTER);
    addEventPanel.add(namePanel);
    // time selection
    JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    this.allDayCheckbox = new JCheckBox("All Day");
    timePanel.add(allDayCheckbox);
    timePanel.add(new JLabel("Start:"));
    this.startTimeCombo = createTimeComboBox();
    timePanel.add(startTimeCombo);
    timePanel.add(new JLabel("End:"));
    this.endTimeCombo = createTimeComboBox();
    timePanel.add(endTimeCombo);
    addEventPanel.add(timePanel);
    // event type selection
    JPanel eventTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ButtonGroup eventTypeGroup = new ButtonGroup();
    this.singleEventRadio = new JRadioButton("Single Event", true);
    this.recurringEventRadio = new JRadioButton("Recurring Event");
    eventTypeGroup.add(singleEventRadio);
    eventTypeGroup.add(recurringEventRadio);
    eventTypePanel.add(singleEventRadio);
    eventTypePanel.add(recurringEventRadio);
    addEventPanel.add(eventTypePanel);
    // recurring options panel
    this.recurringOptionsPanel = createRecurringOptionsPanel();
    recurringOptionsPanel.setVisible(false);
    addEventPanel.add(recurringOptionsPanel);
    // add button
    JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    this.addEventButton = new JButton("Add Event");
    addButtonPanel.add(addEventButton);
    addEventPanel.add(addButtonPanel);
    featuresPanel.add(addEventPanel);
    // calendar management area
    JPanel managePanel = new JPanel(new FlowLayout());
    managePanel.setBorder(BorderFactory.createTitledBorder("Manage"));
    this.changeColorButton = new JButton("Change Color");
    this.createCalendarButton = new JButton("Create Calendar");
    this.editCalendarButton = new JButton("Edit Calendar");
    this.copyDayButton = new JButton("Copy Day's Events");
    this.copyRangeButton = new JButton("Copy Date Range");
    managePanel.add(changeColorButton);
    managePanel.add(createCalendarButton);
    managePanel.add(editCalendarButton);
    managePanel.add(copyDayButton);
    managePanel.add(copyRangeButton);
    featuresPanel.add(managePanel);
    return featuresPanel;
  }

  private JPanel createRecurringOptionsPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    // days of week selection
    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    daysPanel.add(new JLabel("Repeat on:"));
    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    dayCheckboxes = new JCheckBox[7];
    for (int i = 0; i < 7; i++) {
      dayCheckboxes[i] = new JCheckBox(dayNames[i]);
      daysPanel.add(dayCheckboxes[i]);
    }
    panel.add(daysPanel);
    // recurrence end options
    JPanel endOptionsPanel = new JPanel();
    endOptionsPanel.setLayout(new BoxLayout(endOptionsPanel, BoxLayout.Y_AXIS));
    ButtonGroup endGroup = new ButtonGroup();
    // by occurrence option
    JPanel occurrencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    this.byOccurrenceRadio = new JRadioButton("After", true);
    endGroup.add(byOccurrenceRadio);
    occurrencePanel.add(byOccurrenceRadio);
    this.occurrenceSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 999, 1));
    occurrencePanel.add(occurrenceSpinner);
    occurrencePanel.add(new JLabel("occurrences"));
    endOptionsPanel.add(occurrencePanel);
    // by end date option
    JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    this.byEndDateRadio = new JRadioButton("Until");
    endGroup.add(byEndDateRadio);
    endDatePanel.add(byEndDateRadio);
    SpinnerDateModel dateModel = new SpinnerDateModel();
    this.endDateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy");
    endDateSpinner.setEditor(dateEditor);
    endDatePanel.add(endDateSpinner);
    endOptionsPanel.add(endDatePanel);
    panel.add(endOptionsPanel);
    return panel;
  }

  private JComboBox<String> createTimeComboBox() {
    String[] times = new String[48]; // 30-minute intervals
    int index = 0;
    for (int hour = 0; hour < 24; hour++) {
      for (int min = 0; min < 60; min += 30) {
        times[index++] = String.format("%02d:%02d", hour, min);
      }
    }
    return new JComboBox<>(times);
  }

  private void setActionCommands() {
    prevButton.setActionCommand("Previous Month");
    nextButton.setActionCommand("Next Month");
    addEventButton.setActionCommand("Add Event");
    changeColorButton.setActionCommand("Change Color");
    createCalendarButton.setActionCommand("Create Calendar");
    editEventButton.setActionCommand("Edit Event");
    editCalendarButton.setActionCommand("Edit Calendar");
    copyEventButton.setActionCommand("Copy Event");
    copyDayButton.setActionCommand("Copy Day");
    copyRangeButton.setActionCommand("Copy Range");
    calendarDropdown.setActionCommand("Change Calendar");
    // add listeners for ui state changes
    allDayCheckbox.addActionListener(e -> {
      boolean allDay = allDayCheckbox.isSelected();
      startTimeCombo.setEnabled(!allDay);
      endTimeCombo.setEnabled(!allDay);
    });
    recurringEventRadio.addActionListener(e -> {
      recurringOptionsPanel.setVisible(recurringEventRadio.isSelected());
    });
    singleEventRadio.addActionListener(e -> {
      recurringOptionsPanel.setVisible(recurringEventRadio.isSelected());
    });
  }

  @Override
  public void addActionListener(ActionListener listener) {
    prevButton.addActionListener(listener);
    nextButton.addActionListener(listener);
    addEventButton.addActionListener(listener);
    changeColorButton.addActionListener(listener);
    createCalendarButton.addActionListener(listener);
    editEventButton.addActionListener(listener);
    editCalendarButton.addActionListener(listener);
    copyEventButton.addActionListener(listener);
    copyDayButton.addActionListener(listener);
    copyRangeButton.addActionListener(listener);
    calendarDropdown.addActionListener(listener);
  }

  @Override
  public void displayMonth(YearMonth monthToDisplay, Map<String, Color> calendarColors,
                           String activeCalendar, Map<LocalDate, List<String>> eventsOfMonth) {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7));
    monthLabel.setText(monthToDisplay.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
    calendarPanel.setBackground(calendarColors.getOrDefault(activeCalendar, Color.LIGHT_GRAY));
    // add day of week headers
    String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String header : headers) {
      calendarPanel.add(new JLabel(header, SwingConstants.CENTER));
    }
    // add blank spots for the first day of the week
    LocalDate firstDay = monthToDisplay.atDay(1);
    int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 0=sun, 1=mon...
    for (int i = 0; i < dayOfWeek; i++) {
      calendarPanel.add(new JLabel(""));
    }
    // add day buttons
    for (int day = 1; day <= monthToDisplay.lengthOfMonth(); day++) {
      LocalDate date = monthToDisplay.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.setActionCommand("Select Day:" + date);
      dayButton.addActionListener(findActionListener(addEventButton)); // reuse the main listener
      // highlight if events exist
      if (eventsOfMonth.containsKey(date) && !eventsOfMonth.get(date).isEmpty()) {
        dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
      }
      calendarPanel.add(dayButton);
    }
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  // helper to find the controller action listener attached to a component
  private ActionListener findActionListener(JButton button) {
    return button.getActionListeners().length > 0 ? button.getActionListeners()[0] : null;
  }

  @Override
  public void updateEventDetails(LocalDate date, List<String> eventTitles) {
    selectedDayLabel.setText("Events on " + date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
    eventListModel.clear();
    if (eventTitles.isEmpty()) {
      eventListModel.addElement("No events for this day.");
    } else {
      eventTitles.forEach(eventListModel::addElement);
    }
  }

  @Override
  public void updateCalendarDropdown(Set<String> calendarNames, String selected) {
    // temporarily remove listener to prevent firing event on clear
    ActionListener listener = calendarDropdown.getActionListeners().length > 0 ?
            calendarDropdown.getActionListeners()[0] : null;
    if (listener != null) {
      calendarDropdown.removeActionListener(listener);
    }
    calendarDropdown.removeAllItems();
    calendarNames.forEach(calendarDropdown::addItem);
    calendarDropdown.setSelectedItem(selected);
    // re-add listener
    if (listener != null) {
      calendarDropdown.addActionListener(listener);
    }
  }

  @Override
  public String getNewEventText() {
    return newEventField.getText();
  }

  public void clearEventForm() {
    newEventField.setText("");
    singleEventRadio.setSelected(true);
    allDayCheckbox.setSelected(false);
    startTimeCombo.setSelectedIndex(0);
    endTimeCombo.setSelectedIndex(0);
    for (JCheckBox cb : dayCheckboxes) {
      cb.setSelected(false);
    }
    byOccurrenceRadio.setSelected(true);
    occurrenceSpinner.setValue(10);
  }

  public boolean isAllDayEvent() {
    return allDayCheckbox.isSelected();
  }

  public boolean isRecurringEvent() {
    return recurringEventRadio.isSelected();
  }

  public String getStartTime() {
    return (String) startTimeCombo.getSelectedItem();
  }

  public String getEndTime() {
    return (String) endTimeCombo.getSelectedItem();
  }

  public boolean[] getSelectedDays() {
    boolean[] days = new boolean[7];
    for (int i = 0; i < 7; i++) {
      days[i] = dayCheckboxes[i].isSelected();
    }
    return days;
  }

  public boolean isEndByOccurrence() {
    return byOccurrenceRadio.isSelected();
  }

  public int getOccurrences() {
    return (Integer) occurrenceSpinner.getValue();
  }

  public LocalDate getEndDate() {
    return ((java.util.Date) endDateSpinner.getValue()).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
  }

  public List<String> getSelectedEvents() {
    return eventList.getSelectedValuesList();
  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }
}