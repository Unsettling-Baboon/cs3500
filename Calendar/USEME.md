```markdown
# USEME - Calendar Application GUI Guide

## Getting Started
- Launch the application by running the main method
- The application opens with a default calendar named "Default" in UTC timezone
- The main window shows:
  - Calendar grid in the center
  - Navigation controls at the top
  - Event management panel on the right

## Basic Navigation

### Viewing Different Months
- Click the **"<"** button to go to the previous month
- Click the **">"** button to go to the next month
- The current month and year are displayed between the navigation buttons

### Selecting a Day
- Click any day number in the calendar grid to select it
- Selected day's events will appear in the "Events" panel on the right
- Days with events have a blue border

## Calendar Management

### Creating a New Calendar
- Click **"Create Calendar"** button in the Manage section
- Enter a name for your calendar when prompted
- Select or type a timezone from the dropdown (all world timezones available)
- Click OK to create the calendar
- The new calendar becomes the active calendar automatically

### Switching Between Calendars
- Use the **Calendar dropdown** at the top of the window
- Select the calendar you want to view/edit from the list
- The calendar grid updates to show the selected calendar's events

### Editing Calendar Properties
- Click **"Edit Calendar"** button in the Manage section
- Modify the calendar name and/or timezone in the dialog
- Click OK to save changes
- Note: Calendar names must be unique

### Changing Calendar Color
- Click **"Change Color"** button in the Manage section
- Select a new color from the color picker dialog
- The calendar background updates to reflect the new color

## Event Management

### Creating a Single Event

#### All-Day Event
- Select the day you want to add the event to
- Enter the event name in the "Event Name" field
- Check the **"All Day"** checkbox
- Make sure **"Single Event"** is selected
- Click **"Add Event"**

#### Timed Event
- Select the day for the event
- Enter the event name
- Leave "All Day" unchecked
- Select start time from the dropdown (30-minute intervals)
- Select end time from the dropdown
- Make sure **"Single Event"** is selected
- Click **"Add Event"**

### Creating Recurring Events
- Select the starting day for the recurring event
- Enter the event name
- Select **"Recurring Event"** radio button
- Choose the days of the week for recurrence (Sun-Sat checkboxes)
- Choose recurrence end option:
  - **"After"**: Enter number of occurrences
  - **"Until"**: Select an end date
- For timed recurring events:
  - Leave "All Day" unchecked and select start/end times
- For all-day recurring events:
  - Check the "All Day" checkbox
- Click **"Add Event"**

### Editing Events
- Select the day containing the event
- Click on the event in the events list to select it
- Click **"Edit Event"** button
- In the dialog, enter the new event name
- If the event is part of a series, choose:
  - **"This event only"**: Edit just this occurrence
  - **"All events in series"**: Edit all occurrences
  - **"This and future events"**: Edit this and all future occurrences
- Click OK to save changes

### Viewing Events
- Click any day in the calendar to see its events
- Events for the selected day appear in the "Events" panel
- The panel shows the date and lists all events for that day

## Copying Events and Days

### Copying a Single Event
- Select the day containing the event to copy
- Select the event from the events list
- Click **"Copy Event"** button
- Choose the target calendar from the dropdown
- Enter the new date for the event (YYYY-MM-DD format)
- Click OK to copy the event

### Copying All Events from a Day
- Select the day you want to copy events from
- Click **"Copy Day's Events"** button
- In the dialog:
  - Select the target calendar
  - Enter the target date (YYYY-MM-DD format)
- Click OK to copy all events from that day

### Copying Events from a Date Range
- Click **"Copy Date Range"** button
- In the dialog:
  - Enter the start date of the range
  - Enter the end date of the range
  - Select the target calendar
  - Choose copy option:
    - **"Copy to same dates"**: Preserves original dates
    - **"Copy starting from new date"**: Copies to a new date range
- Click OK to copy all events in the range

## Tips and Notes

### Event Validation
- Event names cannot be empty
- End time must be after start time for timed events
- At least one day must be selected for recurring events
- Dates must be in YYYY-MM-DD format

### Calendar Colors
- Each calendar has its own background color
- New calendars are assigned a random pastel color
- Colors help distinguish between different calendars

### Timezone Support
- All timezones are searchable - just type in the dropdown
- Common formats: "US/Eastern", "Europe/London", "Asia/Tokyo"
- UTC is the default for new calendars

### Series Behavior
- Recurring events are treated as a series
- Editing options preserve or break series relationships
- Copied events become standalone (not part of original series)

### Keyboard Shortcuts
- Use Tab to navigate between fields
- Press Enter in text fields to confirm
- Use arrow keys in dropdowns

## Common Workflows

### Planning a Weekly Meeting
1. Select the first meeting date
2. Enter meeting name (e.g., "Team Standup")
3. Select "Recurring Event"
4. Check Monday through Friday
5. Set "After 52 occurrences" for a year
6. Set time 10:00-10:30
7. Click "Add Event"

### Moving Events to Another Calendar
1. Select the source day
2. Use "Copy Day's Events" to copy to another calendar
3. Switch to the original calendar
4. Select and edit each event to remove if needed

### Creating Work and Personal Calendars
1. Create "Work" calendar with your work timezone
2. Create "Personal" calendar with your home timezone
3. Use the dropdown to switch between them
4. Use different colors to distinguish them visually
```