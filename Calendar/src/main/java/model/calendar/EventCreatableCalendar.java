package model.calendar;

/**
 * An interface representing all the things that a {@link EventCreatableCalendar}
 * object should be able to offer its users when creating events.
 */
public interface EventCreatableCalendar {
  /**
   * Create one "normal" event in this calendar that has a subject, start,
   * and end date and time.
   *
   * @param eventSubject              title of this event.
   * @param fromDateStringTtimeString the date and time this event starts.
   * @param toDateStringTtimeString   the date and time this event ends.
   */
  void createSingleNormalEvent(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString
  );

  /**
   * Create recurring, normal events (as defined in the documentation of
   * method {@link #createSingleNormalEvent(String, String, String)}) a
   * total of {@code N} times.
   *
   * @param eventSubject              title of this event.
   * @param fromDateStringTtimeString the date from which these events recur and
   *                                  the common start time these events share.
   * @param toDateStringTtimeString   the date from which these events recur and
   *                                  the common end time these events share.
   * @param repeatWeekdays            days of the week on which these events repeat.
   * @param N                         how many times these events should repeat.
   */
  void createRecurringNormalEventsNTimes(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String repeatWeekdays,
          int N
  );

  /**
   * Create recurring, normal events (as defined in the documentation of
   * method {@link #createSingleNormalEvent(String, String, String)}) a
   * total of {@code N} times.
   *
   * @param eventSubject              title of this event.
   * @param fromDateStringTtimeString the date from which these events recur and
   *                                  the common start time these events share.
   * @param toDateStringTtimeString   the date from which these events recur and
   *                                  the common end time these events share.
   * @param repeatWeekdays            days of the week on which these events repeat.
   * @param untilDateString           the date until which these events should repeat.
   */
  void createRecurringNormalEventsUntilDate(
          String eventSubject,
          String fromDateStringTtimeString,
          String toDateStringTtimeString,
          String repeatWeekdays,
          String untilDateString
  );

  /**
   * Create one "all-day" event that lasts all day during the day it is
   * specified to be on, precisely from 8 A.M. to 5 P.M.
   *
   * @param eventSubject the title of this event.
   * @param dateString   the date this should event should last all-day on.
   */
  void createSingleAllDayEvent(String eventSubject, String dateString);

  /**
   * Create recurring, all-day events (as defined in the documentation of
   * method {@link #createSingleAllDayEvent(String, String)}) a total of
   * {@code N} times.
   *
   * @param eventSubject   title of this event.
   * @param dateString     the date from which these events recur.
   * @param repeatWeekdays days of the week on which these events repeat.
   * @param N              how many times these events should repeat.
   */
  void createRecurringAllDayEventsNTimes(
          String eventSubject,
          String dateString,
          String repeatWeekdays,
          int N
  );

  /**
   * Create recurring, all-day events (as defined in the documentation of
   * method {@link #createSingleAllDayEvent(String, String)}) a total of
   * {@code N} times.
   *
   * @param eventSubject    title of this event.
   * @param dateString      the date from which these events recur.
   * @param repeatWeekdays  days of the week on which these events repeat.
   * @param untilDateString the date until which these events should repeat.
   */
  void createRecurringAllDayEventsUntilDate(
          String eventSubject,
          String dateString,
          String repeatWeekdays,
          String untilDateString
  );
}
