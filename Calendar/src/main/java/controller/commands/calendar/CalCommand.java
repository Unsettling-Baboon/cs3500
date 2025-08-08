package controller.commands.calendar;

import model.calendar.RobustCalendar;

/**
 * An abstract class representing a calendar action command that can
 * be applied on a given calendar model.
 */
public abstract class CalCommand {
  protected final RobustCalendar m;
  protected String
          eventSubject,
          dateString,
          fromDateStringTtimeString,
          toDateStringTtimeString,
          untilDateString,
          repeatWeekdays,
          property,
          newPropertyVal;
  protected int N;

  /**
   * Construct a new calendar command object pointing to a given model.
   *
   * @param m the target model to apply actions to.
   */
  protected CalCommand(RobustCalendar m) {
    this.m = m;
  }

  /**
   * Carry out the action to be done on the model.
   */
  abstract public void perform();
}
