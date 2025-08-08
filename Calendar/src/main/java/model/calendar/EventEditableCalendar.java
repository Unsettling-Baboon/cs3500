package model.calendar;

/**
 * An interface representing all the things that a {@link EventEditableCalendar}
 * object should be able to offer its users when editing events it already has.
 */
public interface EventEditableCalendar {
  /**
   * Assign a new value to the specified property of a single event.
   *
   * @param property                  the property in question.
   * @param eventSubject              the subject of the target event.
   * @param fromDateStringTtimeString the start date and time of the target event.
   * @param toDateStringTtimeString   the end date and time of the target event.
   * @param newPropertyVal            the new value to assign to the specified property.
   */
  void editSingleEvent(
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String newPropertyVal
  );

  /**
   * Assign a new value to the specified property of a single event, and if it is
   * part of a series, do the same for every following event in the series. If not,
   * this has the same effect as the previous method,
   * {@link #editSingleEvent(String, String, String, String, String)}.
   *
   * @param property                  the property in question.
   * @param eventSubject              the subject of the target event.
   * @param fromDateStringTtimeString the start date and time of the target event.
   * @param newPropertyVal            the new value to assign to the specified property.
   */
  void editEventAndMaybeOnward(
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String newPropertyVal
  );

  /**
   * Assign a new value to the specified property of a single event, and if it is
   * part of a series, do the same for all events in the series, before and after.
   * If not, this has the same effect as the first method,
   * {@link #editSingleEvent(String, String, String, String, String)}.
   *
   * @param property                  the property in question.
   * @param eventSubject              the subject of the target event.
   * @param fromDateStringTtimeString the start date and time of the target event.
   * @param newPropertyVal            the new value to assign to the specified property.
   */
  void editEventAndMaybeAll(
          String property,
          String eventSubject,
          String fromDateStringTtimeString,
          String newPropertyVal
  );
}
