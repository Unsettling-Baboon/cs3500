package model.events;

/**
 * An interface representing what an editable calendar event should be able
 * to offer in terms of editing all its properties.
 */
public interface EditableCalEvent extends CalEvent {
  /**
   * Change the subject of this {@link CalEvent}.
   *
   * @param subject the subject to change the {@code subject} to.
   */
  void setSubject(String subject);

  /**
   * Add this event to a {@link RobustCalEvent.Series}. This method
   * should only be accessible when either creating a series or editing a
   * series, not directly on the object itself.
   *
   * @param series the event series to add this event to.
   */
  void setSeries(RobustCalEvent.Series series);

  /**
   * Set this event to last all day on the day it is specified to be on.
   */
  void setAllDay();

  /**
   * Set a new starting date and time for this event.
   *
   * @param startDateStringTtimeString the new start and date time to give this event.
   */
  void setStartDateTime(String startDateStringTtimeString);

  /**
   * Set a new description for this event.
   *
   * @param description the description to give this event.
   */
  void setDescription(String description);

  /**
   * Set a new ending date and time for this event.
   *
   * @param endDateStringTtimeString the new end date and time to give this event.
   */
  void setEndDateTime(String endDateStringTtimeString);

  /**
   * Set a location for this event.
   *
   * @param location a String referring to one of only two explicit
   *                 locations, "physical" or "online"
   */
  void setLocation(String location);

  /**
   * Set a status for this event.
   *
   * @param status a String referring to one of only two explicit
   *               event statuses, "public" or "private"
   */
  void setStatus(String status);
}
